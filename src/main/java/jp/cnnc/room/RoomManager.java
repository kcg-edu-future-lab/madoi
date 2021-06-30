package jp.cnnc.room;

import jp.cnnc.session.WebsocketSession;

public interface RoomManager {
	void onPeerOpen(String key, String roomId, WebsocketSession sessionPeer);
	void onPeerClose(String roomId, String peerId);
	void onPeerMessage(String roomId, String peerId, String message);
	void onPeerMessage(String roomId, String peerId, byte[] message);
}
