package edu.kcg.futurelab.madoi.core.room.logger;

import edu.kcg.futurelab.madoi.core.room.RoomEventLogger;

public class NullRoomEventLogger implements RoomEventLogger{
	@Override
	public void createRoom(String roomId) {
	}
	@Override
	public void receiveOpen(String roomId, String id) {
	}
	@Override
	public void receiveError(String roomId, String peerId, Throwable cause) {
	}
	@Override
	public void receiveClose(String roomId, String peerId) {
	}
	@Override
	public void receiveMessage(String roomId, String peerId, String messageType, String message) {
	}
	@Override
	public void stateChange(String roomId, String state) {
	}
	@Override
	public void sendMessage(String roomId, String name, String[] strings, String messageType, String message) {
	}
}
