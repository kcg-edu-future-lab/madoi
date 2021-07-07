package jp.cnnc.madoi.core.room.eventlogger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.cnnc.madoi.core.Message;
import jp.cnnc.madoi.core.room.RoomEventLogger;

public class OnMemoryEventLogger implements RoomEventLogger{
	@Override
	public void receiveOpen(String roomId, String peerId) {
		events.add(new Object[] {"receiveOpen", roomId, peerId});
	}

	@Override
	public void receiveError(String roomId, String peerId, Throwable cause) {
		events.add(new Object[] {"receiveError", roomId, peerId, cause});
	}

	@Override
	public void receiveClose(String roomId, String peerId) {
		events.add(new Object[] {"receiveClose", roomId, peerId});
	}

	@Override
	public void receiveMessage(String roomId, String peerId, String messageType, String message) {
		events.add(new Object[] {"receiveMessage", roomId, peerId, messageType, message});
	}

	@Override
	public void stateChange(String roomId, String state) {
		events.add(new Object[] {"stateChange", roomId, state});
	}

	@Override
	public void sendMessage(String roomId, String castType, String[] recipients, Message message) {
		try {
			events.add(new Object[] {"sendMessage", roomId, castType, recipients,
					message.getType(), om.writeValueAsString(message)});
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void sendMessage(String roomId, String name, String[] strings, String messageType, String message) {
		events.add(new Object[] {"sendMessage", roomId, name, strings, messageType, message});
	}
	
	public List<Object[]> getEvents() {
		return events;
	}

	private List<Object[]> events = new ArrayList<>();
	private ObjectMapper om = new ObjectMapper();
}
