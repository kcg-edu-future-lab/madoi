package jp.cnnc.madoi.core.message;

import java.util.ArrayList;
import java.util.List;

import jp.cnnc.madoi.core.Message;

public class EnterRoom extends Message{
	public EnterRoom() {
	}

	public EnterRoom(String roomId, String name, int selfId,
			List<String> peers, List<Message> histories) {
		this.roomId = roomId;
		this.name = name;
		this.selfId = selfId;
		this.peers = peers;
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

	public List<String> getPeers() {
		return peers;
	}
	
	public void setPeers(List<String> peers) {
		this.peers = peers;
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
	private List<String> peers = new ArrayList<>();
	private List<Message> histories = new ArrayList<>();
}
