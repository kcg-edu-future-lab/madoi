package jp.cnnc.madoi.core.room;

import java.util.HashMap;
import java.util.Map;

public class DefaultPeer implements Peer{
	public DefaultPeer(MessageSender sender){
		this.sender = sender;
	}

	public void onEnterRoomAllowed(String id, int order, Map<String, Object> profile) {
		this.id = id;
		this.order = order;
		this.profile = profile;
		this.state = State.ENTERED;
	}

	@Override
	public State getState() {
		return state;
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
	public Map<String, Object> getProfile() {
		return profile;
	}

	@Override
	public MessageSender getSender() {
		return sender;
	}

	private State state = State.CONNECTED;
	private String id;
	private int order;
	private Map<String, Object> profile = new HashMap<>();
	private MessageSender sender;
}
