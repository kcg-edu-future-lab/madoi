package jp.cnnc.madoi.core.room;

import java.util.Collection;

public interface RoomManager {
	Peer onPeerOpen(String roomId, MessageSender sender);
	void onPeerError(String roomId, Peer peer, Throwable cause);
	void onPeerClose(String roomId, Peer peer);
	void onPeerMessage(String roomId, Peer peer, String message);
	void onPeerMessage(String roomId, Peer peer, byte[] message);
	Collection<Room> getRooms();
	Room getRoom(String roomId);
}
