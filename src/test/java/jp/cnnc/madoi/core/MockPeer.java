package jp.cnnc.madoi.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.cnnc.madoi.core.message.EnterRoom;
import jp.cnnc.madoi.core.message.Invocation;
import jp.cnnc.madoi.core.message.LeaveRoom;
import jp.cnnc.madoi.core.message.LoginRoom;
import jp.cnnc.madoi.core.message.ObjectState;
import jp.cnnc.madoi.core.message.PeerJoin;
import jp.cnnc.madoi.core.message.PeerLeave;
import jp.go.nict.langrid.repackaged.net.arnx.jsonic.JSON;

public class MockPeer implements Peer {
	public MockPeer(String id, Room room){
		this.id = id;
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
		case "EnterRoom":{m = om.readValue(text, EnterRoom.class); break;}
		case "LeaveRoom":{m = om.readValue(text, LeaveRoom.class); break;}
		case "PeerJoin":{m = om.readValue(text, PeerJoin.class); break;}
		case "PeerLeave":{m = om.readValue(text, PeerLeave.class); break;}
		case "Invocation":{m = om.readValue(text, Invocation.class); break;}
		case "ObjectState":{m = om.readValue(text, ObjectState.class); break;}
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
		room.onPeerMessage(id, JSON.encode(new LoginRoom("", Collections.emptyMap(), Collections.emptyMap())));
	}
	
	public void peerLeave() {
		room.onPeerLeave(id);
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
	private Map<String, Object> profile;
	private List<Message> messages = new ArrayList<>();
	private ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private boolean ioeOnSend;
}

