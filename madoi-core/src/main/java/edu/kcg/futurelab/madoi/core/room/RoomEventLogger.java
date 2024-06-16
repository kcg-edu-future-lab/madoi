package edu.kcg.futurelab.madoi.core.room;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.EvictingQueue;

import edu.kcg.futurelab.madoi.core.message.InvokeMethod;
import edu.kcg.futurelab.madoi.core.message.Message;
import edu.kcg.futurelab.madoi.core.util.JsonUtil;

public interface RoomEventLogger {
	void createRoom(String roomId);
	void receiveOpen(String roomId, String peerId);
	void receiveError(String roomId, String peerId, Throwable cause);
	void receiveClose(String roomId, String peerId);
	void receiveMessage(String roomId, String peerId, String type, String message);

	void stateChange(String roomId, String state);
	default void stateChange(String roomId, Map<Integer, EvictingQueue<InvokeMethod>> queue) {
		try {
			stateChange(roomId, new ObjectMapper().writeValueAsString(queue));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	void sendMessage(String roomId, String castType, String[] recipients, String type, String message);
	default void sendMessage(String roomId, String castType, String recipient, String type, String message) {
		sendMessage(roomId, castType, new String[] {recipient}, type, message);
	}
	default void sendMessage(String roomId, String castType, String[] recipients, Message message) {
		sendMessage(roomId, castType, recipients, message.getType(), JsonUtil.toString(message));
	}
	default void sendMessage(String roomId, String castType, String recipient, Message message) {
		sendMessage(roomId, castType, new String[] {recipient}, message);
	}
}
