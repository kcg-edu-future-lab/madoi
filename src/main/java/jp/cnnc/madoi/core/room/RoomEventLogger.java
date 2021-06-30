package jp.cnnc.madoi.core.room;

import jp.cnnc.madoi.core.Message;
import jp.cnnc.madoi.core.util.JsonUtil;

public interface RoomEventLogger {
	void receiveOpen(String roomId, String peerId);
	void receiveClose(String roomId, String peerId);
	void receiveMessage(String roomId, String peerId, String type, String message);
	default void sendMessage(String roomId, String castType, String[] recipients, Message message) {
		sendMessage(roomId, castType, recipients, message.getType(), JsonUtil.toString(message));
	}
	default void sendMessage(String roomId, String castType, String recipient, Message message) {
		sendMessage(roomId, castType, new String[] {recipient}, message);
	}
	void sendMessage(String roomId, String castType, String[] recipients, String type, String message);
	default void sendMessage(String roomId, String castType, String recipient, String type, String message) {
		sendMessage(roomId, castType, new String[] {recipient}, type, message);
	}
}
