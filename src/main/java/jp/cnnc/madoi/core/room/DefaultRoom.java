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

import jp.cnnc.madoi.core.message.CastType;
import jp.cnnc.madoi.core.message.DefineFunction;
import jp.cnnc.madoi.core.message.EnterRoom;
import jp.cnnc.madoi.core.message.EnterRoomAllowed;
import jp.cnnc.madoi.core.message.InvokeMethod;
import jp.cnnc.madoi.core.message.Message;
import jp.cnnc.madoi.core.message.PeerEntered;
import jp.cnnc.madoi.core.message.PeerInfo;
import jp.cnnc.madoi.core.message.PeerLeaved;
import jp.cnnc.madoi.core.message.Ping;
import jp.cnnc.madoi.core.message.Pong;
import jp.cnnc.madoi.core.message.RoomInfo;
import jp.cnnc.madoi.core.message.UpdateObjectState;
import jp.cnnc.madoi.core.message.UpdatePeerProfile;
import jp.cnnc.madoi.core.message.UpdateRoomProfile;
import jp.cnnc.madoi.core.message.config.ShareConfig.SharingType;
import jp.cnnc.madoi.core.message.definition.FunctionDefinition;
import jp.cnnc.madoi.core.message.definition.ObjectDefinition;

/**
 * Peerは最初は入室待ち状態になる。
 * PeerはRoomにEnterRoomメッセージを送り、承認されればEnterRoomAllowedメッセージが送られ、
 * 参加状態になる。この際、既存のPeerにはPeerEnteredメッセージが送られる。
 */
public class DefaultRoom implements Room{
	public DefaultRoom(String id, Map<String, Object> profile, RoomEventLogger eventLogger) {
		this.id = id;
		this.profile = profile;
		this.eventLogger = eventLogger;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Map<String, Object> getProfile() {
		return profile;
	}

	@Override
	public Map<Integer, ObjectDefinition> getObjectDefinitions() {
		return objectDefinitions;
	}

	@Override
	public Map<Integer, FunctionDefinition> getFunctionDefinitions() {
		return functionDefinitions;
	}

	@Override
	public int getPeerCount() {
		return peers.size();
	}

	@Override
	public Map<Integer, EvictingQueue<InvokeMethod>> getInvocationLogs() {
		return invocationLogs;
	}

	@Override
	public void onRoomCreated() {
		eventLogger.createRoom(id);
	}

	@Override
	public synchronized void onPeerArrive(Peer peer) {
		eventLogger.receiveOpen(id, peer.getId());
		waitingPeers.put(peer.getId(), peer);
	}

	@Override
	public void onPeerError(Peer peer, Throwable cause) {
		eventLogger.receiveError(id, peer.getId(), cause);
	}

	@Override
	public synchronized void onPeerLeave(Peer peer) {
		var peerId = peer.getId();
		System.err.printf("peer #%s removed.%n", peerId);
		waitingPeers.remove(peerId);
		eventLogger.receiveClose(id, peerId);
		peers.remove(peerId);
		if(peers.size() > 0) {
			try {
				castMessage(CastType.BROADCAST, Collections.emptyList(),
					peerId, "PeerLeave", om.writeValueAsString(new PeerLeaved(peerId)));
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
	protected boolean canPeerEnter(Peer peer, EnterRoom loginRoom) {
		return true;
	}

	private void onWaitingPeerMessage(DefaultPeer peer, String message) {
		EnterRoom er = null;
		try {
			er = om.readValue(message, EnterRoom.class);
			if(!canPeerEnter(peer, er)) {
				castMessageTo(CastType.SERVERTOPEER, peer,
						new jp.cnnc.madoi.core.message.Error("Login error."));
				return;
			}
			if(profile == null) {
				profile = er.getRoomProfile() != null ?
					er.getRoomProfile() : new HashMap<>();
			}
			String peerId = null;
			Map<String, Object> peerProfile = null;
			if(er.getSelfPeer() != null) {
				peerId = er.getSelfPeer().getId();
				peerProfile = er.getSelfPeer().getProfile();
			}
			var order = peerOrder++;
			if(peerId == null) peerId = "" + order;
			if(peerProfile == null) peerProfile = new HashMap<>();
			peer.onEntered(peerId, order, peerProfile);
		} catch(JsonProcessingException e) {
			castMessageTo(CastType.SERVERTOPEER, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
			eventLogger.receiveMessage(id, peer.getId(), null, message);
			return;
		}
		if(er.getSelfPeer().getProfile() != null) {
			peer.getProfile().putAll(er.getSelfPeer().getProfile());
		}

		var pe = newPeerEntered(peer);
		for(var p : peers.values()) {
			try {
				p.getSender().send(pe);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		var otherPeers = peers.values().stream().map(p -> newPeerInfo(p))
				.collect(Collectors.toList());
		var histories = new ArrayList<Message>();
		for(var oi : objectInfos.values()) {
			if(oi.getState() != null) {
				histories.add(new UpdateObjectState(oi.getObjectId(), oi.getState(), oi.getRevision()));
			}
		}
		for(Collection<InvokeMethod> ils : invocationLogs.values()) {
			for(InvokeMethod i : ils) {
				histories.add(i);
			}
		}
		var era = new EnterRoomAllowed(
				new RoomInfo(id, profile), newPeerInfo(peer),
				otherPeers, histories);
		peers.put(peer.getId(), peer);
		try {
			eventLogger.sendMessage(id, "SERVERNOTIFY", new String[] {peer.getId()}, era);
			peer.getSender().send(era);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void onPeerMessage(Peer peer, String message) {
		var p = (DefaultPeer)peer;
		if(p.getState().equals(Peer.State.CONNECTED)) {
			onWaitingPeerMessage(p, message);
			return;
		}
		var peerId = p.getId();
		Message m = null;
		try {
			m = decode(peerId, message, Message.class);
			m.setSender(peerId);
		} catch(JsonProcessingException e) {
			castMessageTo(CastType.SERVERTOPEER, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
			eventLogger.receiveMessage(id, p.getId(), null, message);
			return;
		}
		eventLogger.receiveMessage(id, peerId, m.getType(), message);
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
			case "Ping": {
				try {
					var ping = decode(peerId, message, Ping.class);
					var pong = new Pong();
					pong.setBody(ping.getBody());
					castMessageTo(CastType.SERVERTOPEER, peer, pong);
				} catch(JsonProcessingException e) {
					e.printStackTrace();
				}
				break;
			}
			case "UpdateRoomProfile": {
				ct = CastType.BROADCAST;
				recipients.clear();
				try {
					var msg = decode(peerId, message, UpdateRoomProfile.class);
					if(msg.getUpdates() != null) for(Map.Entry<String, Object> e: msg.getUpdates().entrySet()) {
						peer.getProfile().put(e.getKey(), e.getValue());
					}
					if(msg.getDeletes() != null) for(String k: msg.getDeletes()) {
						peer.getProfile().remove(k);
					}
					message = om.writeValueAsString(new UpdateRoomProfile(
							msg.getSender(), msg.getRoomId(), msg.getUpdates(), msg.getDeletes()));
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERTOPEER, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
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
					message = om.writeValueAsString(new UpdatePeerProfile(
							msg.getSender(), msg.getUpdates(), msg.getDeletes()));
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERTOPEER, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
					return;
				}
				break;
			}
			case "DefineObject":{
				ct = CastType.PEERTOSERVER;
				recipients.clear();
				try {
					var def = decode(peerId, message, jp.cnnc.madoi.core.message.DefineObject.class).getDefinition();
					objectDefinitions.put(def.getObjId(), def);
					var methodIndexes = new LinkedHashSet<Integer>();
					for(var mi : def.getMethods()) {
						var sc = mi.getConfig().getShare();
						if(mi.getMethodId() == null || sc == null) continue;
						methodIndexes.add(mi.getMethodId());
						int methodId = mi.getMethodId();
						int maxLog = sc.getMaxLog();
						if(maxLog > 0) {
							invocationLogs.putIfAbsent(methodId, EvictingQueue.<InvokeMethod>create(
									Math.min(maxLog, 10000)));
						}
						if(sc.getType().equals(SharingType.afterExec)) {
							execAndSendMethods.add(methodId);
						}
					}
					objectInfos.computeIfAbsent(def.getObjId(), ObjectInfo::new)
						.setMethods(methodIndexes);
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERTOPEER, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
					return;
				}
				break;
			}
			case "DefineFunction":{
				ct = CastType.PEERTOSERVER;
				recipients.clear();;
				try {
					var def = decode(peerId, message, DefineFunction.class).getDefinition();
					var funcId = def.getFuncId();
					functionDefinitions.put(funcId, def);
					if(def.getConfig().getMaxLog() > 0) {
						invocationLogs.putIfAbsent(funcId, EvictingQueue.<InvokeMethod>create(
								Math.min(def.getConfig().getMaxLog(), 10000)));
					}
					if(SharingType.afterExec.equals(def.getConfig().getType())) {
						execAndSendMethods.add(funcId);
					}
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERTOPEER, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
					return;
				}
				break;
			}
			case "UpdateObjectState": {
				ct = CastType.PEERTOSERVER;
				recipients.clear();;
				try {
					var os = decode(peerId, message, UpdateObjectState.class);
					var oi = objectInfos.computeIfAbsent(os.getObjId(), id->new ObjectInfo(id));
					oi.setState(os.getState());
					oi.setRevision(os.getRevision());
					for(int mi : oi.getMethods()) {
						EvictingQueue<InvokeMethod> q = invocationLogs.get(mi);
						if(q != null) q.clear();
					}
					eventLogger.stateChange(id, invocationLogs);
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERTOPEER, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
					return;
				}
				break;
			}
			case "InvokeMethod": {
				logger.info("InvokeMethod");
				try {
					var iv = decode(peerId, message, InvokeMethod.class);
					var oi = objectInfos.computeIfAbsent(iv.getObjId(), ObjectInfo::new);
					// var cr = oi.getRevision();
					// oiのリビジョンとivのリビジョンを照合する？
					// 実行後にリビジョンが上がるので+1しておく
					oi.setRevision(iv.getObjRevision() + 1);
					message = om.writeValueAsString(iv);
					var fid = iv.getMethodId();
					var q = invocationLogs.get(fid);
					System.out.printf("logs of %d is %d.%n", fid, (q != null) ? q.size() : null);
					if(q != null) q.add(iv);
					if(execAndSendMethods.contains(fid)) {
						ct = CastType.OTHERCAST;
					} else {
						ct = CastType.BROADCAST;
					}
					recipients.clear();
				} catch(JsonProcessingException e) {
					castMessageTo(CastType.SERVERTOPEER, peer, new jp.cnnc.madoi.core.message.Error(e.toString()));
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
		if(ct.equals(CastType.PEERTOSERVER)) return;
		if(ct.equals(CastType.SELFCAST)) {
			eventLogger.sendMessage(id, ct.name(), peer.getId(), m.getType(), message);
			try {
				peer.getSender().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			castMessage(ct, recipients, peer.getId(), m.getType(), message);
		}
	}

	@Override
	public void onPeerMessage(Peer peer, byte[] message) {
	}

	private void castMessageTo(CastType type, Peer peer, Message message) {
		try {
			eventLogger.sendMessage(id, CastType.SERVERTOPEER.name(), peer.getId(), message);
			peer.getSender().send(message);
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

	@Override
	public void castMessage(CastType ct, List<String> recipients, String senderPeerId, String messageType, String message) {
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
					peers.get(id).getSender().sendText(msg.getMessage());
				} catch (IOException e) {
					peers.remove(id);
					System.err.printf("peer #%s removed because of %s.%n", id, e);
					messages.addLast(new Cast(
						CastType.BROADCAST, Collections.emptyList(),
						"__SERVER__", "PeerLeave", encode(new PeerLeaved(id))));
					logger.log(Level.INFO, "Tried to send message to " + id, e);
				}
			}
			eventLogger.sendMessage(id, ct.name(), ids.toArray(new String[]{}),
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

	public static class ObjectInfo{
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

	public Collection<ObjectInfo> getObjectInfos(){
		return objectInfos.values();
	}
	public Collection<Map.Entry<Integer, EvictingQueue<InvokeMethod>>> getMethodInvocations(){
		return invocationLogs.entrySet();
	}

	private String id;
	private Map<String, Object> profile;
	private int peerOrder = 1;
	private RoomEventLogger eventLogger;
	private ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private Map<Integer, ObjectDefinition> objectDefinitions = new LinkedHashMap<>();
	private Map<Integer, FunctionDefinition> functionDefinitions = new LinkedHashMap<>();
	private Map<Integer, ObjectInfo> objectInfos = new LinkedHashMap<>();
	private Map<Integer, EvictingQueue<InvokeMethod>> invocationLogs = new HashMap<>();
//	private Map<Integer, AtomicInteger> objRevision = new HashMap<>();
//	private Map<Integer, String> objectStates = new LinkedHashMap<>();
//	private Map<Integer, Set<Integer>> objectMethods = new HashMap<>();
	private Set<Integer> execAndSendMethods = new HashSet<>();

	private Map<String, Peer> waitingPeers = new HashMap<>();
	private Map<String, Peer> peers = new LinkedHashMap<>();

	private static PeerEntered newPeerEntered(Peer peer) {
		return new PeerEntered(newPeerInfo(peer));
	}

	private static PeerInfo newPeerInfo(Peer peer) {
		return new PeerInfo(peer.getId(), peer.getOrder(), peer.getProfile());
	}

	private static Logger logger = Logger.getLogger(DefaultRoom.class.getName());
}
