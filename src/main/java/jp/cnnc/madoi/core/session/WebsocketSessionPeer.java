package jp.cnnc.madoi.core.session;

import java.io.IOException;
import java.util.Map;

import jakarta.websocket.Session;
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
	public int getOrder() {
		return order;
	}

	@Override
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public Map<String, Object> getProfile() {
		return profile;
	}

	@Override
	public void setProfile(Map<String, Object> profile) {
		this.profile = profile;
	}

	@Override
	public void sendText(String message) throws IOException {
		session.getBasicRemote().sendText(message);
	}

	private Session session;
	private int order;
	private Map<String, Object> profile;
}
