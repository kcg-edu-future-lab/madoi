package jp.cnnc.madoi.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	public void sendText(String text) throws IOException {
		texts.add(text);
	}

	@Override
	public void sendMessage(Message message) throws IOException {
		messages.add(message);
	}

	public void peerArrive() {
		room.onPeerArrive(this);
	}

	public void peerLeave() {
		room.onPeerLeave(id);
	}

	public void peerMessage(Message m) throws JsonProcessingException {
		room.onPeerMessage(id, om.writeValueAsString(m));
	}

	public List<String> getSentTexts() {
		return texts;
	}

	public List<Message> getSentMessages() {
		return messages;
	}

	private String id;
	private Room room;
	private int order;
	private List<String> texts = new ArrayList<>();
	private List<Message> messages = new ArrayList<>();
	private ObjectMapper om = new ObjectMapper();
}
