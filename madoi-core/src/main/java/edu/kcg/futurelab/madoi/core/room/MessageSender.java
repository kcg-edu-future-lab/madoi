package edu.kcg.futurelab.madoi.core.room;

import java.io.IOException;

import edu.kcg.futurelab.madoi.core.message.Message;

public interface MessageSender {
	void sendText(String message) throws IOException;
	void send(Message message) throws IOException;
}
