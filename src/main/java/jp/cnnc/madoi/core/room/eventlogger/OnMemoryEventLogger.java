package jp.cnnc.madoi.core.room.eventlogger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.cnnc.madoi.core.Message;
import jp.cnnc.madoi.core.room.RoomEventLogger;

public class OnMemoryEventLogger implements RoomEventLogger{
	@Override
	public void receiveOpen(String roomId, String id) {
		events.add(new Object[] {"receiveOpen", roomId, id});
	}

	@Override
	public void receiveClose(String roomId, String sessionId) {
		events.add(new Object[] {"receiveClose", roomId, sessionId});
	}

	@Override
	public void receiveMessage(String roomId, String sessionId, String messageType, String message) {
		events.add(new Object[] {"receiveMessage", roomId, sessionId, messageType, message});
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
