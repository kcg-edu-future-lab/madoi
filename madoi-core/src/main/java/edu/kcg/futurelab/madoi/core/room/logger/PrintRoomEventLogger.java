package edu.kcg.futurelab.madoi.core.room.logger;

import java.util.Arrays;

import edu.kcg.futurelab.madoi.core.room.RoomEventLogger;

public class PrintRoomEventLogger implements RoomEventLogger{
	@Override
	public void roomCreate(String roomId) {
		System.err.printf("[%s] roomCreate()%n",
				roomId);
	}

	@Override
	public void roomDestroy(String roomId) {
		System.err.printf("[%s] roomDestroy()%n",
				roomId);
	}

	@Override
	public void peerArrive(String roomId, String peerId) {
		System.err.printf("[%s] peerOpen(%s)%n",
				roomId, peerId);
	}

	@Override
	public void peerLeave(String roomId, String peerId) {
		System.err.printf("[%s] peerClose(%s)%n",
				roomId, peerId);
	}

	@Override
	public void peerMessage(String roomId, String peerId, String messageType, String message) {
		System.err.printf("[%s] peerMessage(%s, %s)%n",
				roomId, peerId, message);
	}

	@Override
	public void peerError(String roomId, String peerId, Throwable cause) {
		System.err.printf("[%s] peerError(%s, %s)%n",
				roomId, peerId, cause.toString());
		cause.printStackTrace(System.err);
	}


	@Override
	public void messageCast(String roomId, String castType, String[] recipients, String messageType, String message) {
		System.err.printf("[%s] messageCast(%s, [%s], %s)%n",
				roomId, castType, Arrays.toString(recipients), message);

	}
}
