package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.Message;

public class LeaveRoom extends Message{
	public LeaveRoom() {
	}

	public LeaveRoom(String roomId) {
		this.roomId = roomId;
	}

	public String getRoomId() {
		return roomId;
	}

	private String roomId;
}
