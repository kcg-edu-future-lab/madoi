package jp.cnnc.madoi.core.room;

import java.io.IOException;

import jp.cnnc.madoi.core.message.Message;

public interface MessageSender {
	void sendText(String message) throws IOException;
	void send(Message message) throws IOException;
}
