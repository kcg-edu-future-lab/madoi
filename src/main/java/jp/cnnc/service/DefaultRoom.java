/*
 * Copyright 2020 Takao Nakaguchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.cnnc.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.EvictingQueue;

import jp.cnnc.Peer;
import jp.cnnc.Room;
import jp.cnnc.Storage;
import jp.cnnc.service.message.Invocation;
import jp.cnnc.service.message.Message;
import jp.cnnc.service.message.MethodConfig;
import jp.cnnc.service.message.MethodConfig.SharingType;
import jp.cnnc.service.message.ObjectConfig;
import jp.cnnc.service.message.ObjectState;
import jp.cnnc.service.message.RoomEnter;
import jp.go.nict.langrid.commons.io.FileUtil;

public class DefaultRoom implements Room{
	public DefaultRoom(String roomId, Storage storage) {
		this.roomId = roomId;
		this.storage = storage;
	}

	@Override
	public boolean canRemove() {
		return peers.size() == 0;
	}

	@Override
	public synchronized void onPeerArrive(Peer session) {
		peers.put(session.getId(), session);
		if(peers.size() == 1) {
			onRoomStarted();
		}
		storage.storeReceiveOpen(session.getId());
		RoomEnter re = new RoomEnter();
		re.setPeerId(clientId.incrementAndGet());
		// statesから状態を送信
		for(Map.Entry<Integer, String> e : states.entrySet()) {
			var state = new ObjectState(e.getKey(), e.getValue());
			re.getHistories().add(state);
		}
		for(Collection<Invocation> ils : invocationLogs.values()) {
			for(Invocation i : ils) {
				re.getHistories().add(i);
			}
		}
		try {
			String message = om.writeValueAsString(re);
			storage.storeSendMessage("SERVERNOTIFY", new String[] {session.getId()}, message);
			session.sendText(message);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized long onPeerClose(String sessionId) {
		peers.remove(sessionId);
		if(peers.size() == 0) {
			storage.storeReceiveClose(sessionId);
			onRoomEnded();
			return 10 * 60 * 1000;
		}
		return -1;
	}

	private void castMessageTo(CastType type, Peer peer, Message message) {
		try {
			String msg = om.writeValueAsString(message);
			storage.storeSendMessage(CastType.SERVERNOTIFY.name(), peer.getId(), msg);
			peer.sendText(msg);
		} catch(JsonProcessingException ex) {
			throw new RuntimeException(ex);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public synchronized void onPeerMessage(String peerId, String message) {
		storage.storeReceiveMessage(peerId, message);
		Peer peer = peers.get(peerId);
		Message m = null;
		try {
			m = om.readValue(message, Message.class);
		} catch(JsonProcessingException e) {
			castMessageTo(CastType.SERVERNOTIFY, peer, new jp.cnnc.service.message.Error(e.toString()));
			return;
		}
		CastType ct = CastType.BROADCAST;
		switch(m.getType()) {
			case "ConnectionConfig":{
				ct = CastType.NONE;
				break;
			}
			case "ObjectConfig":{
				ct = CastType.NONE;
				try {
					var oc = om.readValue(message, ObjectConfig.class);
					objectMethods.put(oc.getObjectIndex(), new LinkedHashSet<>(oc.getMethodIndices()));
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERNOTIFY, peer, new jp.cnnc.service.message.Error(e.toString()));
					return;
				}
				break;
			}
			case "MethodConfig":{
				ct = CastType.NONE;
				try {
					var mc = om.readValue(message, MethodConfig.class);
					int targetIndex = mc.getMethodIndex();
					if(mc.getMaxLog() > 0) {
						invocationLogs.putIfAbsent(targetIndex, EvictingQueue.<Invocation>create(
								Math.max(mc.getMaxLog(), 10000)));
					}
					if(mc.getSharingType().equals(SharingType.SHARE_RESULT)) {
						execAndSendMethods.add(targetIndex);
					}
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERNOTIFY, peer, new jp.cnnc.service.message.Error(e.toString()));
					return;
				}
				break;
			}
			case "ObjectState": {
				ct = CastType.NONE;
				try {
					var os = om.readValue(message, ObjectState.class);
					int objIndex = os.getObjectIndex();
					String state = os.getState();
					states.put(objIndex, state);
					for(int mi : objectMethods.get(objIndex)) {
						EvictingQueue<Invocation> q = invocationLogs.get(mi);
						if(q != null) q.clear();
					}
					break;
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERNOTIFY, peer, new jp.cnnc.service.message.Error(e.toString()));
					return;
				}
			}
			case "Invocation": {
				try {
					var iv = om.readValue(message, Invocation.class);
					int index = iv.getMethodIndex();
					EvictingQueue<Invocation> q = invocationLogs.get(index);
					if(q != null) q.add(iv);
					if(execAndSendMethods.contains(index)) {
						ct = CastType.OTHERCAST;
					} else {
						ct = CastType.BROADCAST;
					}
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERNOTIFY, peer, new jp.cnnc.service.message.Error(e.toString()));
					return;
				}
				break;
			}
			default:
				ct = CastType.BROADCAST;
				break;
		}
		if(ct.equals(CastType.NONE)) return;
		if(ct.equals(CastType.SENDBACK)) {
			storage.storeSendMessage(ct.name(), peer.getId(), message);
			try {
				peer.sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			roomLog.printf(",%n{\"time\": %d, \"sender\": \"%s\", \"message\": %s}",
					new Date().getTime(), peer.getId(), message);
			var ids = new ArrayList<>();
			for(Peer s : peers.values()){
				boolean sender = peer.getId().equals(s.getId());
				if(ct.equals(CastType.OTHERCAST) && sender) continue;
				ids.add(s.getId());
				try {
					s.sendText(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			storage.storeSendMessage(ct.name(), ids.toArray(new String[] {}), message);
		}
	}

	@Override
	public void onPeeerMessage(String peerId, byte[] message) {
	}

	public void onRoomStarted() {
		Date now = new Date();
		String dates = new SimpleDateFormat("yyyyMMdd").format(now);
		String times = new SimpleDateFormat("HHmmss").format(now);
		File dir = new File(new File("logs"), dates);
		dir.mkdirs();
		try {
			File f = FileUtil.createUniqueFile(
					dir,
					getClass().getSimpleName() + "-" + roomId + "-" + times + "-", ".json");
			roomLog = new PrintWriter(Files.newBufferedWriter(f.toPath()));
			roomLog.print("[{}");
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void onRoomEnded() {
		roomLog.printf("%n]%n");
		roomLog.close();
	}

	private String roomId;
	private Storage storage;
	private PrintWriter roomLog;
	private ObjectMapper om = new ObjectMapper();
	private AtomicInteger clientId = new AtomicInteger();

	private Map<Integer, EvictingQueue<Invocation>> invocationLogs = new HashMap<>();
	private Map<Integer, String> states = new LinkedHashMap<>();
	private Map<Integer, Set<Integer>> objectMethods = new HashMap<>();
	private Set<Integer> execAndSendMethods = new HashSet<>();

	private Map<String, Peer> peers = new LinkedHashMap<>();
}
