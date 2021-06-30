package jp.cnnc.madoi.core.room;

import jp.cnnc.madoi.core.session.WebsocketSessionPeer;

public interface RoomManager {
	void onPeerOpen(String key, String roomId, WebsocketSessionPeer sessionPeer);
	void onPeerClose(String roomId, String peerId);
	void onPeerMessage(String roomId, String peerId, String message);
	void onPeerMessage(String roomId, String peerId, byte[] message);
}
