package jp.cnnc.madoi.core.session;

import java.io.IOException;

import jp.cnnc.madoi.core.Message;
import jp.cnnc.madoi.core.Peer;
import jp.cnnc.madoi.core.util.JsonUtil;

public class WebsocketSession implements Peer{
	public WebsocketSession(javax.websocket.Session session){
		this.session = session;
	}

	@Override
	public String getId() {
		return session.getId();
	}

	@Override
	public void sendText(String message) throws IOException {
		session.getBasicRemote().sendText(message);
	}

	@Override
	public void sendMessage(Message message) throws IOException {
		session.getBasicRemote().sendText(JsonUtil.toString(message));
	}

	private javax.websocket.Session session;
}
