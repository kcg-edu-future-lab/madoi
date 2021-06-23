package edu.kcg.futurelab.middleman.storage;

import edu.kcg.futurelab.middleman.Storage;

public class NullStorage implements Storage{
	@Override
	public void storeReceiveOpen(String id) {
	}
	@Override
	public void storeReceiveClose(String sessionId) {
	}
	@Override
	public void storeReceiveMessage(String sessionId, String message) {
	}
	@Override
	public void storeReceiveMessage(String sessionId, byte[] message) {
	}
	@Override
	public void storeSendMessage(String name, String[] strings, String message) {
	}
	@Override
	public void storeSendMessage(String name, String[] strings, byte[] message) {
	}
}
