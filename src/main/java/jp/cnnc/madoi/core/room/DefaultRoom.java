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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.cnnc.madoi.core.message.CastType;
import jp.cnnc.madoi.core.message.DefineFunction;
import jp.cnnc.madoi.core.message.DefineObject;
import jp.cnnc.madoi.core.message.EnterRoom;
import jp.cnnc.madoi.core.message.EnterRoomAllowed;
import jp.cnnc.madoi.core.message.InvokeFunction;
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
	public Map<Integer, ObjectRuntimeInfo> getObjectRuntimeInfos() {
		return objectRuntimeInfos;
	}

	@Override
	public Map<Integer, FunctionRuntimeInfo> getFunctionRuntimeInfos() {
		return functionRuntimeInfos;
	}

	@Override
	public List<History> getMessageHistories(){
		return histories;
	}

	@Override
	public Collection<Peer> getPeers() {
		return peers.values();
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
		var histories = new ArrayList<Object>();
		for(var oi : objectRuntimeInfos.values()) {
			if(oi.getState() != null) {
				histories.add(new UpdateObjectState(
						oi.getDefinition().getObjId(), oi.getState(), oi.getRevision()));
			}
		}
		for(var h : this.histories) {
			histories.add(h.getMessage());
		}
		var era = new EnterRoomAllowed(
				new RoomInfo(id, profile), newPeerInfo(peer),
				otherPeers, histories);
		peers.put(peer.getId(), peer);
		try {
			eventLogger.sendMessage(id, "SERVERTOPEER", new String[] {peer.getId()}, era);
			peer.getSender().send(era);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized void onPeerMessage(Peer peer, String message) {
		var p = (DefaultPeer)peer;
		if(p.getState().equals(Peer.State.CONNECTED)) {
			onWaitingPeerMessage(p, message);
			return;
		}
		var peerId = p.getId();
		var m = decodeAndSetSender(peer, message);
		if(m == null) {
			eventLogger.receiveMessage(id, p.getId(), null, message);
			return;
		}
		eventLogger.receiveMessage(id, peerId, m.get("type").toString(), message);

		switch(m.get("type").toString()) {
			case "Ping": {
				var ping = decodeAndSetSender(peer, message, Ping.class);
				var pong = new Pong();
				pong.setBody(ping.getBody());
				castFromServerToPeer(pong, peer);
				break;
			}
			case "UpdateRoomProfile": {
				var msg = decodeAndSetSender(peer, message, UpdateRoomProfile.class);
				if(msg.getUpdates() != null) for(Map.Entry<String, Object> e: msg.getUpdates().entrySet()) {
					peer.getProfile().put(e.getKey(), e.getValue());
				}
				if(msg.getDeletes() != null) for(String k: msg.getDeletes()) {
					peer.getProfile().remove(k);
				}
				forwardBroadcast(msg);
				break;
			}
			case "UpdatePeerProfile": {
				var msg = decodeAndSetSender(peer, message, UpdatePeerProfile.class);
				if(msg.getUpdates() != null) for(Map.Entry<String, Object> e: msg.getUpdates().entrySet()) {
					peer.getProfile().put(e.getKey(), e.getValue());
				}
				if(msg.getDeletes() != null) for(String k: msg.getDeletes()) {
					peer.getProfile().remove(k);
				}
				forwardOthercast(msg);
				break;
			}
			case "DefineObject": {
				var def = decodeAndSetSender(peer, message, DefineObject.class).getDefinition();
				objectRuntimeInfos.put(def.getObjId(), new ObjectRuntimeInfo(def));
				break;
			}
			case "DefineFunction": {
				var def = decodeAndSetSender(peer, message, DefineFunction.class).getDefinition();
				functionRuntimeInfos.put(def.getFuncId(), new FunctionRuntimeInfo(def));
				break;
			}
			case "UpdateObjectState": {
				var uos = decodeAndSetSender(peer, message, UpdateObjectState.class);
				var ori = objectRuntimeInfos.get(uos.getObjId());
				if(ori == null) {
					var msg = String.format("unknown object id: %d", uos.getObjId());
					logger.warning(msg);
					castFromServerToPeer(newError(msg), peer);
					return;
				}
				ori.setState(uos.getState());
				if(ori.getRevision() != uos.getRevision()) {
					var msg = String.format(
							"Object revision not match. It's possible to be inconsistent. objId: %d, rev: %d, newRev: %d.",
							uos.getObjId(), ori.getRevision(), uos.getRevision());
					logger.warning(msg);
				}
				ori.setRevision(uos.getRevision());
				// 更新されたオブジェクトに対するInvokeMethod履歴を削除
				var it = histories.iterator();
				while(it.hasNext()) {
					var h = it.next();
					if(!(h.getMessage() instanceof InvokeMethod)) continue;
					var imh = (InvokeMethod)h.getMessage();
					if(imh.getObjId() == uos.getObjId()) it.remove();
				}
				histories.add(MessageHistory.of(uos));
				if(histories.size() >= maxHistory) {
					histories.remove(0);
				}
				break;
			}
			case "InvokeMethod": {
				var im = decodeAndSetSender(peer, message, InvokeMethod.class);
				var ori = objectRuntimeInfos.get(im.getObjId());
				if(ori == null) {
					var msg = String.format("unknown object id: %d", im.getObjId());
					logger.warning(msg);
					castFromServerToPeer(newError(msg), peer);
					return;
				}
				var mri = ori.getMethods().get(im.getMethodId());
				if(mri == null) {
					var msg = String.format(
							"Method not found. objId: %d, methodId: %d.",
							im.getObjId(), im.getMethodId());
					logger.warning(msg);
					castFromServerToPeer(newError(msg), peer);
					break;
				}
				// 実行後にリビジョンが上がるので+1しておく
				if(ori.getRevision() != im.getObjRevision()) {
					var msg = String.format(
							"Object revision not match. It's possible to be inconsistent. objId: %d, rev: %d, newRev: %d.",
							im.getObjId(), ori.getRevision(), im.getObjRevision());
					logger.warning(msg);
				}
				ori.setRevision(im.getObjRevision() + 1);
				mri.onInvoked();
				// 履歴に追加
				histories.add(MessageHistory.of(im));
				if(histories.size() >= maxHistory) {
					histories.remove(0);
				}
				// 送信
				var md = mri.getDefinition();
				if(md.getConfig().getShare().getType().equals(SharingType.beforeExec)) {
					forwardBroadcast(im);
				} else {
					forwardOthercast(im);
				}
				break;
			}
			case "InvokeFunction": {
				var ifn = decodeAndSetSender(peer, message, InvokeFunction.class);
				var fri = functionRuntimeInfos.get(ifn.getFuncId());
				if(fri == null) {
					var msg = String.format(
							"Function not found. funcId: %d.",
							ifn.getFuncId());
					logger.warning(msg);
					castFromServerToPeer(newError(msg), peer);
					break;
				}
				// 履歴に追加
				histories.add(MessageHistory.of(ifn));
				if(histories.size() >= maxHistory) {
					histories.remove(0);
				}
				// 送信
				var fd = fri.getDefinition();
				if(fd.getConfig().getType().equals(SharingType.beforeExec)) {
					forwardBroadcast(ifn);
				} else {
					forwardOthercast(ifn);
				}
				break;
			}
			default:{
				histories.add(new SOMHistory(m));
				if(histories.size() >= maxHistory) {
					histories.remove(0);
				}
				castMessage(m);
				break;
			}
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
					var p = peers.get(id);
					if(p != null) p.getSender().sendText(msg.getMessage());
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

	private void castMessage(Map<String, Object> message) {
		castMessage(
				CastType.valueOf(message.get("castType").toString()),
				Arrays.asList((String[])message.get("recipients")),
				message.get("sender").toString(),
				message.get("type").toString(),
				encode(message));
	}

	private void castMessage(Message message) {
		castMessage(
				message.getCastType(), Arrays.asList(message.getRecipients()),
				message.getSender(), message.getType(),
				encode(message));
/*
		var recipients = new ArrayList<Peer>();
		switch(message.getCastType()) {
			case UNICAST:
			case SERVERTOPEER:
				recipients.add(peers.get(message.getRecipients()[0]));
				break;
			case MULTICAST:
				for(var r : message.getRecipients()) {
					recipients.add(peers.get(r));
				}
				break;
			case BROADCAST:
				for(var p : peers.values()) {
					recipients.add(p);
				}
				break;
			case SELFCAST:
				recipients.add(peers.get(message.getSender()));
				break;
			case OTHERCAST:
				for(var p : peers.values()) {
					if(p.getId().equals(message.getSender())) continue;
					recipients.add(p);
				}
				break;
			case PEERTOSERVER:
		}
		var m = encode(message);
		var failPeers = new ArrayList<Peer>();
		for(var p : recipients) {
			try{
				p.getSender().sendText(m);
			} catch (IOException e) {
				failPeers.add(p);
			}
		}
		for(var p : failPeers) {
			if(peers.containsKey(p.getId())) {
				onPeerLeave(p);
			}
		}*/
	}

	private void castFromServerToPeer(Message message, Peer peer) {
		message.setSender("__SERVER__");
		message.setCastType(CastType.SERVERTOPEER);
		message.setRecipients(new String[] {peer.getId()});
		castMessage(message);
	}

	private void forwardBroadcast(Message message) {
		message.setCastType(CastType.BROADCAST);
		castMessage(message);
	}

	private void forwardOthercast(Message message) {
		message.setCastType(CastType.OTHERCAST);
		castMessage(message);
	}

	private jp.cnnc.madoi.core.message.Error newError(String message) {
		var e = new jp.cnnc.madoi.core.message.Error(message);
		e.setSender("__SERVER__");
		e.setCastType(CastType.SERVERTOPEER);
		return e;
	}

	private <T extends Message> T decodeAndSetSender(Peer peer, String message, Class<T> clazz){
		try {
			var m = om.readValue(message, clazz);
			m.setSender(peer.getId());
			return m;
		} catch(JsonProcessingException e) {
			logger.log(Level.WARNING, "failed to parse message from " + peer.getId() + ", messge: " + message);
			castFromServerToPeer(newError(e.toString()), peer);
			return null;
		}
	}

	private Map<String, Object> decodeAndSetSender(Peer peer, String message){
		try {
			var m = om.readValue(
					message, new TypeReference<Map<String, Object>>(){});
			m.put("sender", peer.getId());
			return m;
		} catch(JsonProcessingException e) {
			logger.log(Level.WARNING, "failed to parse message from " + peer.getId() + ", messge: " + message);
			castFromServerToPeer(newError(e.toString()), peer);
			return null;
		}
	}

	private String encode(Object o) {
		try {
			return om.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private String id;
	private Map<String, Object> profile;
	private int peerOrder = 1;
	private RoomEventLogger eventLogger;
	private ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private Map<Integer, ObjectRuntimeInfo> objectRuntimeInfos = new LinkedHashMap<>();
	private Map<Integer, FunctionRuntimeInfo> functionRuntimeInfos = new LinkedHashMap<>();

	/*
	 * 履歴のサイズは有限。古いものから消す。
	 * オブジェクトの状態+メソッド実行の場合でも、オブジェクトの状態が一番古い履歴で履歴がもう満杯なら、オブジェクトの状態を消す。
	 * いずれオブジェクトの状態がきて同期状態に戻るはず。
	 */
	static class MessageHistory<T extends Message> implements History{
		private T message;
		public MessageHistory(T message) {
			this.message = message;
		}
		@Override
		public T getMessage() {
			return message;
		}
		@Override
		public String getMessageType() {
			return message.getType();
		}
		public static <U extends Message> MessageHistory<U> of(U message){
			return new MessageHistory<U>(message);
		}
	}
	static class SOMHistory implements History{
		private Map<String, Object> message;
		public SOMHistory(Map<String, Object> message) {
			this.message = message;
		}
		@Override
		public Object getMessage() {
			return message;
		}
		@Override
		public String getMessageType() {
			return message.get("type").toString();
		}
	}
	private LinkedList<History> histories = new LinkedList<>();
	private int maxHistory = 1024;

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
