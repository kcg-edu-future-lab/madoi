package edu.kcg.futurelab.madoi.core.websocket;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kcg.futurelab.madoi.core.message.Message;
import edu.kcg.futurelab.madoi.core.room.Connection;
import jakarta.websocket.Session;

public class WebSocketConnection implements Connection{
	public WebSocketConnection(Session session) {
		this.session = session;
	}
	@Override
	public void sendText(String message) throws IOException {
		session.getBasicRemote().sendText(message);
	}
	@Override
	public void send(Message message) throws IOException{
		session.getBasicRemote().sendText(om.writeValueAsString(message));
	}
	@Override
	public void close() throws IOException {
		session.close();
	}

	private Session session;
	private static ObjectMapper om = new ObjectMapper();
}
