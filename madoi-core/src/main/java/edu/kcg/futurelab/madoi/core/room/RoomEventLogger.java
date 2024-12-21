package edu.kcg.futurelab.madoi.core.room;

import edu.kcg.futurelab.madoi.core.message.Message;
import edu.kcg.futurelab.madoi.core.util.JsonUtil;

public interface RoomEventLogger {
	void roomCreate(String roomId);
	void roomDestroy(String roomId);
	void peerArrive(String roomId, String peerId);
	void peerLeave(String roomId, String peerId);
	void peerMessage(String roomId, String peerId, String type, String message);
	void peerError(String roomId, String peerId, Throwable cause);

	void messageCast(String roomId, String castType, String[] recipients, String type, String message);
	default void messageCast(String roomId, String castType, String recipient, String type, String message) {
		messageCast(roomId, castType, new String[] {recipient}, type, message);
	}
	default void messageCast(String roomId, String castType, String[] recipients, Message message) {
		messageCast(roomId, castType, recipients, message.getType(), JsonUtil.toString(message));
	}
	default void messageCast(String roomId, String castType, String recipient, Message message) {
		messageCast(roomId, castType, new String[] {recipient}, message);
	}
}
