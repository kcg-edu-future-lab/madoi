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
import java.util.List;
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
import jp.cnnc.madoi.core.message.FunctionDefinition;
import jp.cnnc.madoi.core.message.Invocation;
import jp.cnnc.madoi.core.message.LoginRoom;
import jp.cnnc.madoi.core.message.ObjectState;
import jp.cnnc.madoi.core.message.PeerInfo;
import jp.cnnc.madoi.core.message.PeerJoin;
import jp.cnnc.madoi.core.message.PeerLeave;
import jp.cnnc.madoi.core.message.UpdatePeerProfile;
import jp.cnnc.madoi.core.message.config.ShareConfig.SharingType;

/**
 * Peerは最初は入室待ち状態になる。
 * PeerはRoomにRoomLoginメッセージを送り、承認されればEnterRoomメッセージが送られ、
 * 参加状態になる。この際、既存のPeerにはPeerEnterメッセージが送られる。
 * @author Takao Nakaguchi
 */
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
	public synchronized void onPeerArrive(Peer peer) {
		eventLogger.receiveOpen(roomId, peer.getId());
		waitingPeers.put(peer.getId(), peer);
	}

	@Override
	public void onPeerError(String peerId, Throwable cause) {
		eventLogger.receiveError(roomId, peerId, cause);
	}

	@Override
	public synchronized void onPeerLeave(String peerId) {
		waitingPeers.remove(peerId);
		eventLogger.receiveClose(roomId, peerId);
		peers.remove(peerId);
		if(peers.size() > 0) {
			try {
				castMessage(CastType.BROADCAST, Collections.emptyList(),
					peerId, "PeerLeave", om.writeValueAsString(new PeerLeave(peerId)));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * このルームに入っても良いかを判定する。
	 * @param peer ピア
	 * @param loginRoom LoginRoomメッセージ
	 * @return
	 */
	protected boolean canPeerEnter(Peer peer, LoginRoom loginRoom) {
		return true;
	}
	
	private void onWaitingPeerMessage(Peer peer, String message) {
		LoginRoom lr = null;
		try {
			lr = decode(peer.getId(), message, LoginRoom.class);
			if(!canPeerEnter(peer, lr)) {
				castMessageTo(CastType.SERVERTOCLIENT, peer,
						new jp.cnnc.madoi.core.message.Error("Login error."));
				return;
			}
			peer.setOrder(peerOrder++);
		} catch(JsonProcessingException e) {
			castMessageTo(CastType.SERVERTOCLIENT, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
			eventLogger.receiveMessage(roomId, peer.getId(), null, message);
			return;
		}
		peer.setProfile(lr.getPeerProfile());

		for(Peer p : peers.values()) {
			try {
				p.sendMessage(new PeerJoin(new PeerInfo(peer.getId(), peer.getOrder(), peer.getProfile())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		EnterRoom er = new EnterRoom();
		er.setSelf(new EnterRoom.SelfPeer(peer.getId(), peer.getOrder()));
		er.setPeers(new ArrayList<>(
				peers.values().stream().map(p -> new PeerInfo(p.getId(), p.getOrder(), p.getProfile()))
				.collect(Collectors.toList())
				));
		// statesから状態を送信
		for(var oi : objectInfos.values()) {
			if(oi.getState() != null) {
				er.getHistories().add(new ObjectState(oi.getObjectId(), oi.getState(), oi.getRevision()));
			}
		}
		for(Collection<Invocation> ils : invocationLogs.values()) {
			for(Invocation i : ils) {
				er.getHistories().add(i);
			}
		}
		peers.put(peer.getId(), peer);
		try {
			eventLogger.sendMessage(roomId, "SERVERNOTIFY", new String[] {peer.getId()}, er);
			peer.sendMessage(er);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void onPeerMessage(String peerId, String message) {
		var wp = waitingPeers.remove(peerId);
		if(wp != null) {
			onWaitingPeerMessage(wp, message);
			return;
		}

		var peer = peers.get(peerId);
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
		List<String> recipients = new ArrayList<>();
		try{
			Object mct = om.readValue(message, Map.class).get("castType");
			if(mct != null){
					ct = CastType.valueOf(mct.toString());
			}
			Object r = om.readValue(message, Map.class).get("recipients");
			if(r != null && r instanceof List){
				recipients = (List<String>)r;
				if(recipients.size() > 0) String.class.cast(recipients.get(0));
			}
		} catch(IllegalArgumentException | JsonProcessingException | ClassCastException e){
			logger.log(Level.WARNING, "failed to read castType or recipients", e);
		}
		switch(m.getType()) {
			case "ConnectionInfo":{
				ct = CastType.CLIENTTOSERVER;
				recipients.clear();;
				break;
			}
			case "ObjectDefinition":{
				ct = CastType.CLIENTTOSERVER;
				recipients.clear();
				try {
					var oc = decode(peerId, message, jp.cnnc.madoi.core.message.ObjectDefinition.class);
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
					objectInfos.computeIfAbsent(oc.getObjId(), ObjectInfo::new)
						.setMethods(methodIndexes);
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERTOCLIENT, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
					return;
				}
				break;
			}
			case "FunctionDefinition":{
				ct = CastType.CLIENTTOSERVER;
				recipients.clear();;
				try {
					var fi = decode(peerId, message, FunctionDefinition.class);
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
			case "UpdatePeerProfile": {
				ct = CastType.OTHERCAST;
				recipients.clear();
				try {
					var msg = decode(peerId, message, UpdatePeerProfile.class);
					if(msg.getUpdates() != null) for(Map.Entry<String, Object> e: msg.getUpdates().entrySet()) {
						peer.getProfile().put(e.getKey(), e.getValue());
					}
					if(msg.getDeletes() != null) for(String k: msg.getDeletes()) {
						peer.getProfile().remove(k);
					}
					msg.setPeerId(peerId);
					message = om.writeValueAsString(msg);
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERTOCLIENT, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
					return;
				}
				break;
			}
			case "ObjectState": {
				ct = CastType.CLIENTTOSERVER;
				recipients.clear();;
				try {
					var os = decode(peerId, message, ObjectState.class);
					var oi = objectInfos.computeIfAbsent(os.getObjId(), id->new ObjectInfo(id));
					oi.setState(os.getState());
					oi.setRevision(os.getRevision());
					for(int mi : oi.getMethods()) {
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
					if(iv.getObjId() != null) {
						var oi = objectInfos.computeIfAbsent(iv.getObjId(), ObjectInfo::new);
						// var cr = oi.getRevision();
						// oiのリビジョンとivのリビジョンを照合する？
						// 実行後にリビジョンが上がるので+1しておく
						oi.setRevision(iv.getObjRevision() + 1);
					}
					message = om.writeValueAsString(iv);
					var fid = iv.getFuncId();
					EvictingQueue<Invocation> q = invocationLogs.get(fid);
					if(q != null) q.add(iv);
					if(execAndSendMethods.contains(fid)) {
						ct = CastType.OTHERCAST;
					} else {
						ct = CastType.BROADCAST;
					}
					recipients.clear();
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERTOCLIENT, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
					return;
				}
				break;
			}
			default:{
				try {
					var msg = om.readValue(message, Map.class);
					msg.put("sender", peerId);
					message = om.writeValueAsString(msg);
				} catch(JsonProcessingException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		if(ct == null){
			ct = CastType.BROADCAST;
			recipients.clear();
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
			castMessage(ct, recipients, peer.getId(), m.getType(), message);
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

	public static class Cast{
		private CastType castType;
		private List<String> recipients;
		private String senderPeerId;
		private String messateType;
		private String message;
		public Cast(CastType castType, List<String> recipients, String senderPeerId, String messateType, String message) {
			this.castType = castType;
			this.recipients = recipients;
			this.senderPeerId = senderPeerId;
			this.messateType = messateType;
			this.message = message;
		}
		public CastType getCastType() {
			return castType;
		}
		public void setCastType(CastType castType) {
			this.castType = castType;
		}
		public List<String> getRecipients() {
			return recipients;
		}
		public void setRecipients(List<String> recipients) {
			this.recipients = recipients;
		}
		public String getSenderPeerId() {
			return senderPeerId;
		}
		public void setSenderPeerId(String senderPeerId) {
			this.senderPeerId = senderPeerId;
		}
		public String getMessateType() {
			return messateType;
		}
		public void setMessateType(String messateType) {
			this.messateType = messateType;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
	}

	private void castMessage(CastType ct, List<String> recipients, String senderPeerId, String messageType, String message) {
		var messages = new LinkedList<Cast>();
		messages.add(new Cast(ct, recipients, senderPeerId, messageType, message));
		while(messages.size() > 0) {
			var msg = messages.pollFirst();
			var ids = new ArrayList<String>();
			if(msg.getCastType().equals(CastType.UNICAST)){
				if(msg.getRecipients().size() == 1){
					ids.add(msg.recipients.get(0));
				}
			} else if(msg.getCastType().equals(CastType.MULTICAST)){
				ids.addAll(msg.getRecipients());
			} else if(msg.getCastType().equals(CastType.BROADCAST)){
				for(var id : peers.keySet()){
					ids.add(id);
				}
			} else if(msg.getCastType().equals(CastType.OTHERCAST)){
				for(var p : peers.entrySet()){
					var id = p.getKey();
					if(id.equals(senderPeerId)) continue;
					ids.add(id);
				}
			}
			for(var id : ids){
				try {
					peers.get(id).sendText(msg.getMessage());
				} catch (IOException e) {
					peers.remove(id);
					messages.addLast(new Cast(
						CastType.BROADCAST, Collections.emptyList(),
						"__SYSTEM__", "PeerLeave", encode(new PeerLeave(id))));
					logger.log(Level.INFO, "Tried to send message to " + id, e);
				}
			}
			eventLogger.sendMessage(roomId, ct.name(), ids.toArray(new String[]{}),
				msg.getMessateType(), msg.getMessage());
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
	private int peerOrder = 1;
	private RoomEventLogger eventLogger;
	private ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private static class ObjectInfo{
		public ObjectInfo(int id) {
			this.objectId = id;
		}
		public int getObjectId() {
			return objectId;
		}
//		public EvictingQueue<Invocation> getInvocationLogs() {
//			return invocationLogs;
//		}
//		public void setInvocationLogs(EvictingQueue<Invocation> invocationLogs) {
//			this.invocationLogs = invocationLogs;
//		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public int getRevision() {
			return revision;
		}
		public void setRevision(int revision) {
			this.revision = revision;
		}
		public Set<Integer> getMethods() {
			return methods;
		}
		public void setMethods(Set<Integer> methods) {
			this.methods = methods;
		}
//		public Set<Integer> getExecAndSendMethods() {
//			return execAndSendMethods;
//		}
//		public void setExecAndSendMethods(Set<Integer> execAndSendMethods) {
//			this.execAndSendMethods = execAndSendMethods;
//		}

		private int objectId;
//		private EvictingQueue<Invocation> invocationLogs;
		private String state;
		private int revision = -1;
		private Set<Integer> methods = new HashSet<>();
//		private Set<Integer> execAndSendMethods = new HashSet<>();;
	}

	private Map<Integer, ObjectInfo> objectInfos = new LinkedHashMap<>();
	private Map<Integer, EvictingQueue<Invocation>> invocationLogs = new HashMap<>();
//	private Map<Integer, AtomicInteger> objRevision = new HashMap<>();
//	private Map<Integer, String> objectStates = new LinkedHashMap<>();
//	private Map<Integer, Set<Integer>> objectMethods = new HashMap<>();
	private Set<Integer> execAndSendMethods = new HashSet<>();

	private Map<String, Peer> waitingPeers = new HashMap<>();
	private Map<String, Peer> peers = new LinkedHashMap<>();

	private static Logger logger = Logger.getLogger(DefaultRoom.class.getName());
}
