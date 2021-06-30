package jp.cnnc.message;

import java.util.ArrayList;
import java.util.List;

public class RoomEnter extends Message{
	public RoomEnter() {
	}

	public RoomEnter(String roomId, String name, int selfId, List<Message> histories) {
		this.roomId = roomId;
		this.name = name;
		this.selfId = selfId;
		this.histories = histories;
	}

	public String getRoomId() {
		return roomId;
	}


	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getSelfId() {
		return selfId;
	}


	public void setPeerId(int selfId) {
		this.selfId = selfId;
	}


	public List<Message> getHistories() {
		return histories;
	}


	public void setHistories(List<Message> histories) {
		this.histories = histories;
	}

	private String roomId;
	private String name;
	private int selfId;
	private List<Message> histories = new ArrayList<Message>();
}
