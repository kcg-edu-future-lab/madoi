package jp.cnnc.storage;

import java.util.Arrays;

import jp.cnnc.Storage;

public class PrintStorage implements Storage{
	public PrintStorage(String roomId) {
		this.roomId = roomId;
	}
	
	@Override
	public void storeReceiveOpen(String peerId) {
		System.err.printf("[%s] receiveOpen(%s)%n",
				roomId, peerId);
	}

	@Override
	public void storeReceiveClose(String peerId) {
		System.err.printf("[%s] receiveClose(%s)%n",
				roomId, peerId);
	}

	@Override
	public void storeReceiveMessage(String peerId, String message) {
		System.err.printf("[%s] receiveMessage(%s, %s)%n",
				roomId, peerId, message);
	}

	@Override
	public void storeReceiveMessage(String peerId, byte[] message) {
		System.err.printf("[%s] receiveMessage(%s, [%d bytes bin])%n",
				roomId, peerId, message.length);
	}

	@Override
	public void storeSendMessage(String castType, String[] recipients, String message) {
		System.err.printf("[%s] sendMessagee(%s, [%s], %s)%n",
				roomId, castType, Arrays.toString(recipients), message);
		
	}

	@Override
	public void storeSendMessage(String castType, String[] recipients, byte[] message) {
		System.out.printf("[%s] sendMessage(%s, [%s], [%d bytes bin])%n",
				roomId, castType, Arrays.toString(recipients), message.length);
	}

	private String roomId;
}
