package edu.kcg.futurelab.middleman;

public interface Storage {
	void storeReceiveOpen(String peerId);
	void storeReceiveClose(String peerId);
	void storeReceiveMessage(String peerId, String message);
	void storeReceiveMessage(String peerId, byte[] message);
	void storeSendMessage(String castType, String[] recipients, String message);
	void storeSendMessage(String castType, String[] recipients, byte[] message);
	default void storeSendMessage(String castType, String peerId, String message) {
		storeSendMessage(castType, new String[] {peerId}, message);
	}
	default void storeSendMessage(String castType, String peerId, byte[] message) {
		storeSendMessage(castType, new String[] {peerId}, message);
	}
}
