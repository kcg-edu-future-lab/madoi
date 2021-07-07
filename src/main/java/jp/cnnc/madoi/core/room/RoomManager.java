package jp.cnnc.madoi.core.room;

import java.util.Collection;

import jp.cnnc.madoi.core.Room;
import jp.cnnc.madoi.core.session.WebsocketSessionPeer;

public interface RoomManager {
	void onPeerOpen(String key, String roomId, WebsocketSessionPeer sessionPeer);
	void onPeerError(String roomId, String peerId, Throwable cause);
	void onPeerClose(String roomId, String peerId);
	void onPeerMessage(String roomId, String peerId, String message);
	void onPeerMessage(String roomId, String peerId, byte[] message);
	Collection<Room> getRooms();
	Room getRoom(String roomId);
}
