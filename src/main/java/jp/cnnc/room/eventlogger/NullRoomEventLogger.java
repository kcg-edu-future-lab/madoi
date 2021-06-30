package jp.cnnc.room.eventlogger;

import jp.cnnc.room.RoomEventLogger;

public class NullRoomEventLogger implements RoomEventLogger{
	@Override
	public void receiveOpen(String roomId, String id) {
	}
	@Override
	public void receiveClose(String roomId, String sessionId) {
	}
	@Override
	public void receiveMessage(String roomId, String sessionId, String messageType, String message) {
	}
	@Override
	public void receiveMessage(String roomId, String sessionId, String messageType, byte[] message) {
	}
	@Override
	public void sendMessage(String roomId, String name, String[] strings, String messageType, String message) {
	}
	@Override
	public void sendMessage(String roomId, String name, String[] strings, String messageType, byte[] message) {
	}
}
