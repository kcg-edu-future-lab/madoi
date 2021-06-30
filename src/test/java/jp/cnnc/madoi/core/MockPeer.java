package jp.cnnc.madoi.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MockPeer implements Peer {
	public MockPeer(String id){
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void sendText(String text) throws IOException {
		texts.add(text);
	}

	@Override
	public void sendMessage(Message message) throws IOException {
		messages.add(message);
	}

	public List<String> getSentTexts() {
		return texts;
	}

	public List<Message> getSentMessages() {
		return messages;
	}

	private String id;
	private List<String> texts = new ArrayList<>();
	private List<Message> messages = new ArrayList<>();
}
