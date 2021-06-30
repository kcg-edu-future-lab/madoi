package jp.cnnc.room.eventlogger;

import java.util.Arrays;

import jp.cnnc.room.RoomEventLogger;

public class PrintRoomEventLogger implements RoomEventLogger{
	@Override
	public void receiveOpen(String roomId, String peerId) {
		System.err.printf("[%s] receiveOpen(%s)%n",
				roomId, peerId);
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
	public void receiveMessage(String roomId, String peerId, String messageType, byte[] message) {
		System.err.printf("[%s] receiveMessage(%s, [%d bytes bin])%n",
				roomId, peerId, message.length);
	}

	@Override
	public void sendMessage(String roomId, String castType, String[] recipients, String messageType, String message) {
		System.err.printf("[%s] sendMessagee(%s, [%s], %s)%n",
				roomId, castType, Arrays.toString(recipients), message);
		
	}

	@Override
	public void sendMessage(String roomId, String castType, String[] recipients, String messageType, byte[] message) {
		System.out.printf("[%s] sendMessage(%s, [%s], [%d bytes bin])%n",
				roomId, castType, Arrays.toString(recipients), message.length);
	}
}
