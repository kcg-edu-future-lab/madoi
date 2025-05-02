package edu.kcg.futurelab.madoi.core.room;

import java.util.Map;

public interface Peer {
	public enum State{
		CONNECTED, ENTERED, LEAVED
	}
	State getState();
	String getId();
	int getOrder();
	Map<String, Object> getProfile();
	Connection getConnection();

	void setAttributes(String id, int order, Map<String, Object> profile);
}
