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
package jp.cnnc.madoi.core.room;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.EvictingQueue;

import jp.cnnc.madoi.core.CastType;
import jp.cnnc.madoi.core.Message;
import jp.cnnc.madoi.core.Peer;
import jp.cnnc.madoi.core.Room;
import jp.cnnc.madoi.core.message.EnterRoom;
import jp.cnnc.madoi.core.message.Invocation;
import jp.cnnc.madoi.core.message.MethodConfig;
import jp.cnnc.madoi.core.message.MethodConfig.SharingType;
import jp.cnnc.madoi.core.message.ObjectConfig;
import jp.cnnc.madoi.core.message.ObjectState;
import jp.cnnc.madoi.core.message.PeerInfo;
import jp.cnnc.madoi.core.message.PeerJoin;
import jp.cnnc.madoi.core.message.PeerLeave;

public class DefaultRoom implements Room{
	public DefaultRoom(String roomId, RoomEventLogger storage) {
		this.roomId = roomId;
		this.eventLogger = storage;
	}

	@Override
	public String getRoomId() {
		return roomId;
	}

	@Override
	public int getPeerCount() {
		return peers.size();
	}

	@Override
	public Map<Integer, EvictingQueue<Invocation>> getInvocationLogs() {
		return invocationLogs;
	}

	@Override
	public synchronized void onPeerArrive(Peer session) {
		for(Peer p : peers.values()) {
			try {
				p.sendMessage(new PeerJoin(session.getId()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(peers.size() == 0) {
			onRoomStarted();
		}
		eventLogger.receiveOpen(roomId, session.getId());
		EnterRoom re = new EnterRoom();
		re.setRoomId(this.roomId);
		re.setPeerId(clientId.incrementAndGet());
		re.setPeers(new ArrayList<>(
				peers.values().stream().map(p -> new PeerInfo(p.getId(), p.getOrder()))
				.collect(Collectors.toList())
				));
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
		peers.put(session.getId(), session);
		try {
			eventLogger.sendMessage(roomId, "SERVERNOTIFY", new String[] {session.getId()}, re);
			session.sendMessage(re);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized void onPeerLeave(String peerId) {
		eventLogger.receiveClose(roomId, peerId);
		peers.remove(peerId);
		if(peers.size() > 0) {
			PeerLeave pl = new PeerLeave(peerId);
			for(Peer p : peers.values()) {
				try {
					p.sendMessage(pl);
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void castMessageTo(CastType type, Peer peer, Message message) {
		try {
			eventLogger.sendMessage(roomId, CastType.SERVERNOTIFY.name(), peer.getId(), message);
			peer.sendMessage(message);
		} catch(JsonProcessingException ex) {
			throw new RuntimeException(ex);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private <T extends Message> T decode(String sender, String message, Class<T> clazz)
	throws JsonMappingException, JsonProcessingException{
		var m = om.readValue(message, clazz);
		m.setSender(sender);
		return m;
	}
	@Override
	public synchronized void onPeerMessage(String peerId, String message) {
		Peer peer = peers.get(peerId);
		Message m = null;
		try {
			m = decode(peerId, message, Message.class);
			m.setSender(peerId);
		} catch(JsonProcessingException e) {
			castMessageTo(CastType.SERVERNOTIFY, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
			eventLogger.receiveMessage(roomId, peerId, null, message);
			return;
		}
		eventLogger.receiveMessage(roomId, peerId, m.getType(), message);
		CastType ct = CastType.BROADCAST;
		switch(m.getType()) {
			case "ConnectionConfig":{
				ct = CastType.NONE;
				break;
			}
			case "ObjectConfig":{
				ct = CastType.NONE;
				try {
					var oc = decode(peerId, message, ObjectConfig.class);
					objectMethods.put(oc.getObjectIndex(), new LinkedHashSet<>(oc.getMethodIndices()));
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERNOTIFY, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
					return;
				}
				break;
			}
			case "MethodConfig":{
				ct = CastType.NONE;
				try {
					var mc = decode(peerId, message, MethodConfig.class);
					int targetIndex = mc.getMethodIndex();
					if(mc.getMaxLog() > 0) {
						invocationLogs.putIfAbsent(targetIndex, EvictingQueue.<Invocation>create(
								Math.max(mc.getMaxLog(), 10000)));
					}
					if(mc.getSharingType().equals(SharingType.SHARE_RESULT)) {
						execAndSendMethods.add(targetIndex);
					}
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERNOTIFY, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
					return;
				}
				break;
			}
			case "ObjectState": {
				ct = CastType.NONE;
				try {
					var os = decode(peerId, message, ObjectState.class);
					int objIndex = os.getObjectIndex();
					String state = os.getState();
					states.put(objIndex, state);
					for(int mi : objectMethods.getOrDefault(objIndex, Collections.emptySet())) {
						EvictingQueue<Invocation> q = invocationLogs.get(mi);
						if(q != null) q.clear();
					}
					eventLogger.stateChange(roomId, invocationLogs);
					break;
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERNOTIFY, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
					return;
				}
			}
			case "Invocation": {
				try {
					var iv = decode(peerId, message, Invocation.class);
					int index = iv.getMethodIndex();
					EvictingQueue<Invocation> q = invocationLogs.get(index);
					if(q != null) q.add(iv);
					if(execAndSendMethods.contains(index)) {
						ct = CastType.OTHERCAST;
					} else {
						ct = CastType.BROADCAST;
					}
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERNOTIFY, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
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
			eventLogger.sendMessage(roomId, ct.name(), peer.getId(), m.getType(), message);
			try {
				peer.sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
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
			eventLogger.sendMessage(roomId, ct.name(), ids.toArray(new String[] {}), m.getType(), message);
		}
	}

	@Override
	public void onPeerMessage(String peerId, byte[] message) {
	}

	@Override
	public void onRoomStarted() {
	}

	@Override
	public void onRoomEnded() {
	}

	private String roomId;
	private RoomEventLogger eventLogger;
	private ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	private AtomicInteger clientId = new AtomicInteger();

	private Map<Integer, EvictingQueue<Invocation>> invocationLogs = new HashMap<>();
	private Map<Integer, String> states = new LinkedHashMap<>();
	private Map<Integer, Set<Integer>> objectMethods = new HashMap<>();
	private Set<Integer> execAndSendMethods = new HashSet<>();

	private Map<String, Peer> peers = new LinkedHashMap<>();
}
