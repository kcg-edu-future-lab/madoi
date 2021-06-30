package jp.cnnc.madoi.core.room;

public interface RoomEventLogger {
	void receiveOpen(String roomId, String peerId);
	void receiveClose(String roomId, String peerId);
	void receiveMessage(String roomId, String peerId, String messageType, String message);
	void receiveMessage(String roomId, String peerId, String messageType, byte[] message);
	void sendMessage(String roomId, String castType, String[] recipients, String messageType, String message);
	void sendMessage(String roomId, String castType, String[] recipients, String messageType, byte[] message);
	default void sendMessage(String roomId, String castType, String peerId, String messageType, String message) {
		sendMessage(roomId, castType, new String[] {peerId}, messageType, message);
	}
	default void sendMessage(String roomId, String castType, String peerId, String messageType, byte[] message) {
		sendMessage(roomId, castType, new String[] {peerId}, messageType, message);
	}
}
