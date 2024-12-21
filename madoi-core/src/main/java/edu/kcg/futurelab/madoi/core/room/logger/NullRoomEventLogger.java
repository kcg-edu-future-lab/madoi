package edu.kcg.futurelab.madoi.core.room.logger;

import edu.kcg.futurelab.madoi.core.room.RoomEventLogger;

public class NullRoomEventLogger implements RoomEventLogger{
	@Override
	public void roomCreate(String roomId) {
	}
	@Override
	public void roomDestroy(String roomId) {
	}
	@Override
	public void peerArrive(String roomId, String id) {
	}
	@Override
	public void peerLeave(String roomId, String peerId) {
	}
	@Override
	public void peerMessage(String roomId, String peerId, String messageType, String message) {
	}
	@Override
	public void peerError(String roomId, String peerId, Throwable cause) {
	}
	@Override
	public void messageCast(String roomId, String name, String[] strings, String messageType, String message) {
	}
}
