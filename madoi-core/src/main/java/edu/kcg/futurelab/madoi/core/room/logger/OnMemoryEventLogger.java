package edu.kcg.futurelab.madoi.core.room.logger;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kcg.futurelab.madoi.core.message.Message;
import edu.kcg.futurelab.madoi.core.room.RoomEventLogger;

public class OnMemoryEventLogger implements RoomEventLogger{
	@Override
	public void roomCreate(String roomId) {
		events.add(new Object[] {"roomCreate", roomId});
	}

	@Override
	public void roomDestroy(String roomId) {
		events.add(new Object[] {"roomDestroy", roomId});
	}

	@Override
	public void peerArrive(String roomId, String peerId) {
		events.add(new Object[] {"peerOpen", roomId, peerId});
	}
	@Override
	public void peerLeave(String roomId, String peerId) {
		events.add(new Object[] {"peerClose", roomId, peerId});
	}
	@Override
	public void peerMessage(String roomId, String peerId, String messageType, String message) {
		events.add(new Object[] {"peerMessage", roomId, peerId, messageType, message});
	}
	@Override
	public void peerError(String roomId, String peerId, Throwable cause) {
		events.add(new Object[] {"peerError", roomId, peerId, cause});
	}
	@Override
	public void messageCast(String roomId, String castType, String[] recipients, Message message) {
		try {
			events.add(new Object[] {"messageCast", roomId, castType, recipients,
					message.getType(), om.writeValueAsString(message)});
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void messageCast(String roomId, String name, String[] strings, String messageType, String message) {
		events.add(new Object[] {"messageCast", roomId, name, strings, messageType, message});
	}

	public List<Object[]> getEvents() {
		return events;
	}

	private List<Object[]> events = new ArrayList<>();
	private ObjectMapper om = new ObjectMapper();
}
