package jp.cnnc.madoi.core.message;

import java.util.ArrayList;
import java.util.List;

import jp.cnnc.madoi.core.Message;

public class EnterRoom extends Message{
	public EnterRoom() {
	}

	public EnterRoom(String roomId, String name, String peerId,
			List<PeerInfo> peers, List<Message> histories) {
		this.roomId = roomId;
		this.name = name;
		this.peerId = peerId;
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

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public List<PeerInfo> getPeers() {
		return peers;
	}
	
	public void setPeers(List<PeerInfo> peers) {
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
	private String peerId;
	private List<PeerInfo> peers = new ArrayList<>();
	private List<Message> histories = new ArrayList<>();
}