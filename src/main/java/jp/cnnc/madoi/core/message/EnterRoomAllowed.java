package jp.cnnc.madoi.core.message;

import java.util.ArrayList;
import java.util.List;

public class EnterRoomAllowed extends Message{
	public EnterRoomAllowed() {
	}
	public EnterRoomAllowed(
			RoomInfo room,
			PeerInfo selfPeer, List<PeerInfo> otherPeers, List<Message> histories) {
		this.room = room;
		this.selfPeer = selfPeer;
		this.otherPeers = otherPeers;
		this.histories = histories;
	}

	public RoomInfo getRoom() {
		return room;
	}
	public void setRoom(RoomInfo room) {
		this.room = room;
	}
	public PeerInfo getSelfPeer() {
		return selfPeer;
	}
	public void setSelfPeer(PeerInfo self) {
		this.selfPeer = self;
	}
	public List<PeerInfo> getOtherPeers() {
		return otherPeers;
	}
	public void setOtherPeers(List<PeerInfo> peers) {
		this.otherPeers = peers;
	}
	public List<Message> getHistories() {
		return histories;
	}
	public void setHistories(List<Message> histories) {
		this.histories = histories;
	}

	private RoomInfo room;
	private PeerInfo selfPeer;
	private List<PeerInfo> otherPeers = new ArrayList<>();
	private List<Message> histories = new ArrayList<>();
}
