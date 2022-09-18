package jp.cnnc.madoi.core;

import java.io.IOException;
import java.util.Map;

import jp.cnnc.madoi.core.util.JsonUtil;

public interface Peer {
	String getId();
	int getOrder();
	void setOrder(int order);
	Map<String, Object> getProfile();
	void setProfile(Map<String, Object> profile);
	void sendText(String message) throws IOException;
	default void sendMessage(Message message) throws IOException{
		sendText(JsonUtil.toString(message));
	}
}
