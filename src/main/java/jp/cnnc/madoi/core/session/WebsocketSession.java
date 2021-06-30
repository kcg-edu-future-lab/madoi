package jp.cnnc.madoi.core.session;

import java.io.IOException;

import jp.cnnc.madoi.core.Peer;

public class WebsocketSession implements Peer{
    public WebsocketSession(javax.websocket.Session session){
        this.session = session;
    }

    @Override
    public String getId() {
        return session.getId();
    }
    @Override
    public void sendText(String string) throws IOException {
        session.getBasicRemote().sendText(string);
    }
    private javax.websocket.Session session;
}
