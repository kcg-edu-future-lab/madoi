package jp.cnnc.madoi.core.room;

import java.util.Map;

public interface Peer {
	public enum State{
		CONNECTED, ENTERED, LEAVED
	}
	State getState();
	String getId();
	int getOrder();
	Map<String, Object> getProfile();
	MessageSender getSender();
}
