package jp.cnnc.madoi.core.session;

import java.io.IOException;

import javax.websocket.Session;

import jp.cnnc.madoi.core.Peer;

public class WebsocketSessionPeer implements Peer{
	public WebsocketSessionPeer(Session session){
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

	private Session session;
}
