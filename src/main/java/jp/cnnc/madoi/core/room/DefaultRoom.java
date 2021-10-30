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
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import jp.cnnc.madoi.core.message.FunctionInfo;
import jp.cnnc.madoi.core.message.Invocation;
import jp.cnnc.madoi.core.message.ObjectInfo;
import jp.cnnc.madoi.core.message.ObjectState;
import jp.cnnc.madoi.core.message.PeerInfo;
import jp.cnnc.madoi.core.message.PeerJoin;
import jp.cnnc.madoi.core.message.PeerLeave;
import jp.cnnc.madoi.core.message.config.ShareConfig.SharingType;
import jp.go.nict.langrid.commons.util.Trio;

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
	public void onRoomStarted() {
	}

	@Override
	public void onRoomEnded() {
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
		re.setPeerId(session.getId());
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
	public void onPeerError(String peerId, Throwable cause) {
		eventLogger.receiveError(roomId, peerId, cause);
	}

	@Override
	public synchronized void onPeerLeave(String peerId) {
		eventLogger.receiveClose(roomId, peerId);
		peers.remove(peerId);
		if(peers.size() > 0) {
			try {
				castMessage(CastType.BROADCAST, peerId, "PeerLeave", om.writeValueAsString(new PeerLeave(peerId)));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public synchronized void onPeerMessage(String peerId, String message) {
		Peer peer = peers.get(peerId);
		Message m = null;
		try {
			m = decode(peerId, message, Message.class);
			m.setSender(peerId);
		} catch(JsonProcessingException e) {
			castMessageTo(CastType.SERVERTOCLIENT, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
			eventLogger.receiveMessage(roomId, peerId, null, message);
			return;
		}
		eventLogger.receiveMessage(roomId, peerId, m.getType(), message);
		CastType ct = CastType.BROADCAST;
		switch(m.getType()) {
			case "ConnectionInfo":{
				ct = CastType.CLIENTTOSERVER;
				break;
			}
			case "ObjectInfo":{
				ct = CastType.CLIENTTOSERVER;
				try {
					var oc = decode(peerId, message, ObjectInfo.class);
					var methodIndexes = new LinkedHashSet<Integer>();
					for(var mi : oc.getMethods()) {
						var sc = mi.getConfig().getShare();
						if(mi.getFuncId() == null || sc == null) continue;
						methodIndexes.add(mi.getFuncId());
						int funcId = mi.getFuncId();
						int maxLog = sc.getMaxLog();
						if(maxLog > 0) {
							invocationLogs.putIfAbsent(funcId, EvictingQueue.<Invocation>create(
									Math.min(maxLog, 10000)));
						}
						if(sc.getType().equals(SharingType.afterExec)) {
							execAndSendMethods.add(funcId);
						}
					}
					objectMethods.put(oc.getObjId(), methodIndexes);
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERTOCLIENT, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
					return;
				}
				break;
			}
			case "FunctionInfo":{
				ct = CastType.CLIENTTOSERVER;
				try {
					var fi = decode(peerId, message, FunctionInfo.class);
					var funcId = fi.getFuncId();
					if(fi.getConfig().getMaxLog() > 0) {
						invocationLogs.putIfAbsent(funcId, EvictingQueue.<Invocation>create(
								Math.min(fi.getConfig().getMaxLog(), 10000)));
					}
					if(fi.getConfig().getType().equals(SharingType.afterExec)) {
						execAndSendMethods.add(funcId);
					}
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERTOCLIENT, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
					return;
				}
				break;
			}
			case "ObjectState": {
				ct = CastType.CLIENTTOSERVER;
				try {
					var os = decode(peerId, message, ObjectState.class);
					int objId = os.getObjId();
					String state = os.getState();
					states.put(objId, state);
					for(int mi : objectMethods.getOrDefault(objId, Collections.emptySet())) {
						EvictingQueue<Invocation> q = invocationLogs.get(mi);
						if(q != null) q.clear();
					}
					eventLogger.stateChange(roomId, invocationLogs);
					break;
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERTOCLIENT, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
					return;
				}
			}
			case "Invocation": {
				try {
					var iv = decode(peerId, message, Invocation.class);
					message = om.writeValueAsString(iv);
					var fid = iv.getFuncId();
					EvictingQueue<Invocation> q = invocationLogs.get(fid);
					if(q != null) q.add(iv);
					if(execAndSendMethods.contains(fid)) {
						ct = CastType.OTHERCAST;
					} else {
						ct = CastType.BROADCAST;
					}
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERTOCLIENT, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
					return;
				}
				break;
			}
			default:
				ct = null;
				break;
		}
		if(ct == null){
			ct = CastType.BROADCAST;
			try{
				Object mct = om.readValue(message, Map.class).get("castType");
				if(mct != null){
						ct = CastType.valueOf(mct.toString());
				}
			} catch(IllegalArgumentException | JsonProcessingException e){
			}
		}
		if(ct.equals(CastType.CLIENTTOSERVER)) return;
		if(ct.equals(CastType.SELFCAST)) {
			eventLogger.sendMessage(roomId, ct.name(), peer.getId(), m.getType(), message);
			try {
				peer.sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			castMessage(ct, peer.getId(), m.getType(), message);
		}
	}

	@Override
	public void onPeerMessage(String peerId, byte[] message) {
	}

	private void castMessageTo(CastType type, Peer peer, Message message) {
		try {
			eventLogger.sendMessage(roomId, CastType.SERVERTOCLIENT.name(), peer.getId(), message);
			peer.sendMessage(message);
		} catch(JsonProcessingException ex) {
			throw new RuntimeException(ex);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void castMessage(CastType ct, String senderPeerId, String messageType, String message) {
		var messages = new LinkedList<Trio<CastType, String, String>>();
		messages.add(Trio.create(ct, messageType, message));
		while(messages.size() > 0) {
			var msg = messages.pollFirst();
			var ids = new ArrayList<>();
			var it = peers.entrySet().iterator();
			while(it.hasNext()) {
				var p = it.next().getValue();
				if(msg.getFirst().equals(CastType.OTHERCAST) &&
						p.getId().equals(senderPeerId)) continue;
				ids.add(p.getId());
				try {
					p.sendText(msg.getThird());
				} catch (IOException e) {
					it.remove();
					messages.addLast(Trio.create(CastType.BROADCAST, "PeerLeave", encode(new PeerLeave(p.getId()))));
					logger.log(Level.INFO, "Tried to send message to " + p.getId(), e);
				}
			}
			eventLogger.sendMessage(roomId, ct.name(), ids.toArray(new String[] {}), msg.getSecond(), message);
		}
	}

	private <T extends Message> T decode(String sender, String message, Class<T> clazz)
	throws JsonMappingException, JsonProcessingException{
		var m = om.readValue(message, clazz);
		m.setSender(sender);
		return m;
	}

	private String encode(Object o) {
		try {
			return om.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private String roomId;
	private RoomEventLogger eventLogger;
	private ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private Map<Integer, EvictingQueue<Invocation>> invocationLogs = new HashMap<>();
	private Map<Integer, String> states = new LinkedHashMap<>();
	private Map<Integer, Set<Integer>> objectMethods = new HashMap<>();
	private Set<Integer> execAndSendMethods = new HashSet<>();

	private Map<String, Peer> peers = new LinkedHashMap<>();

	private static Logger logger = Logger.getLogger(DefaultRoom.class.getName());
}
