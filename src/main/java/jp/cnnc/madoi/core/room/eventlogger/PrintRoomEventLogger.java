package jp.cnnc.madoi.core.room.eventlogger;

import java.util.Arrays;

import jp.cnnc.madoi.core.room.RoomEventLogger;

public class PrintRoomEventLogger implements RoomEventLogger{
	@Override
	public void createRoom(String roomId) {
		System.err.printf("[%s] createRoom()%n",
				roomId);
	}

	@Override
	public void receiveOpen(String roomId, String peerId) {
		System.err.printf("[%s] receiveOpen(%s)%n",
				roomId, peerId);
	}

	@Override
	public void receiveError(String roomId, String peerId, Throwable cause) {
		System.err.printf("[%s] receiveError(%s, %s)%n",
				roomId, peerId, cause.toString());
		cause.printStackTrace(System.err);
	}

	@Override
	public void receiveClose(String roomId, String peerId) {
		System.err.printf("[%s] receiveClose(%s)%n",
				roomId, peerId);
	}

	@Override
	public void receiveMessage(String roomId, String peerId, String messageType, String message) {
		System.err.printf("[%s] receiveMessage(%s, %s)%n",
				roomId, peerId, message);
	}

	@Override
	public void stateChange(String roomId, String state) {
		System.err.printf("[%s] stateChange(%s, %s)%n",
				roomId, state);
	}

	@Override
	public void sendMessage(String roomId, String castType, String[] recipients, String messageType, String message) {
		System.err.printf("[%s] sendMessagee(%s, [%s], %s)%n",
				roomId, castType, Arrays.toString(recipients), message);

	}
}
