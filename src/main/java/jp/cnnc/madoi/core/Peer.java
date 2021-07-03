package jp.cnnc.madoi.core;

import java.io.IOException;

import jp.cnnc.madoi.core.util.JsonUtil;

public interface Peer {
	String getId();
	int getOrder();
	void setOrder(int order);
	void sendText(String message) throws IOException;
	default void sendMessage(Message message) throws IOException{
		sendText(JsonUtil.toString(message));
	}
}
