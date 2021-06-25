package jp.cnnc.message;

import java.util.ArrayList;
import java.util.List;

public class RoomEnter extends Message{
	private String id;
	private String name;
	private int selfId;
	private List<Message> histories = new ArrayList<Message>();


	public RoomEnter() {
	}

	public RoomEnter(String id, String name, int selfId, List<Message> histories) {
		this.id = id;
		this.name = name;
		this.selfId = selfId;
		this.histories = histories;
	}

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
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
}
