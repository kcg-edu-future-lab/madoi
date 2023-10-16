package jp.cnnc.madoi.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.cnnc.madoi.core.message.CustomMessage;
import jp.cnnc.madoi.core.message.EnterRoomAllowed;
import jp.cnnc.madoi.core.message.InvokeMethodOrFunction;
import jp.cnnc.madoi.core.message.LeaveRoom;
import jp.cnnc.madoi.core.message.EnterRoom;
import jp.cnnc.madoi.core.message.NotifyObjectState;
import jp.cnnc.madoi.core.message.PeerEntered;
import jp.cnnc.madoi.core.message.PeerLeaved;
import jp.cnnc.madoi.core.message.PeerProfileUpdated;
import jp.go.nict.langrid.repackaged.net.arnx.jsonic.JSON;

public class MockPeer implements Peer {
	public MockPeer(String id, String name, Room room){
		this.id = id;
		this.profile.put("name", name);
		this.room = room;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public Map<String, Object> getProfile() {
		return profile;
	}

	@Override
	public void setProfile(Map<String, Object> profile) {
		this.profile = profile;
	}

	public void setIoeOnSend(boolean ioeOnSend) {
		this.ioeOnSend = ioeOnSend;
	}

	@Override
	public void sendText(String text) throws IOException {
		if(ioeOnSend) throw new IOException();
		Message m = om.readValue(text, Message.class);
		switch(m.getType()) {
		case "EnterRoom":{m = om.readValue(text, EnterRoomAllowed.class); break;}
		case "LeaveRoom":{m = om.readValue(text, LeaveRoom.class); break;}
		case "PeerJoin":{m = om.readValue(text, PeerEntered.class); break;}
		case "PeerLeave":{m = om.readValue(text, PeerLeaved.class); break;}
		case "UpdatePeerProfile":{m = om.readValue(text, PeerProfileUpdated.class); break;}
		case "Invocation":{m = om.readValue(text, InvokeMethodOrFunction.class); break;}
		case "ObjectState":{m = om.readValue(text, NotifyObjectState.class); break;}
		default:{m = om.readValue(text, CustomMessage.class); break;}
		}
		messages.add(m);
	}

	@Override
	public void sendMessage(Message message) throws IOException {
		if(ioeOnSend) throw new IOException();
		messages.add(message);
	}

	public void peerArriveAndLoginRoom() {
		peerArrive();
		loginRoom();
	}

	public void peerArrive() {
		room.onPeerArrive(this);
	}

	public void loginRoom() {
		room.onPeerMessage(id, JSON.encode(new EnterRoom("", Collections.emptyMap(), profile)));
	}

	public void peerLeave() {
		room.onPeerLeave(id);
	}

	@SuppressWarnings("serial")
	public void updatePeerProfileChangeName(String name) {
		profile.put("name", name);
		room.onPeerMessage(id, JSON.encode(new PeerProfileUpdated(
				id, new HashMap<>() {{put("name", name);}}, null)));
	}

	public void peerMessage(Message m) throws JsonProcessingException {
		room.onPeerMessage(id, om.writeValueAsString(m));
	}

	public List<Message> getSentMessages() {
		return messages;
	}

	public int getSentMessageCount() {
		return messages.size();
	}
	
	public Message getSentMessageAt(int index) {
		return messages.get(index);
	}

	private String id;
	private Room room;
	private int order;
	private Map<String, Object> profile = new HashMap<>();
	private List<Message> messages = new ArrayList<>();
	private ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private boolean ioeOnSend;
}

