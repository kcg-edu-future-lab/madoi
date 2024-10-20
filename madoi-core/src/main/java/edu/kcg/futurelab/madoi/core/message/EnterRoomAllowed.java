package edu.kcg.futurelab.madoi.core.message;

import java.util.ArrayList;
import java.util.List;

import edu.kcg.futurelab.madoi.core.message.info.PeerInfo;
import edu.kcg.futurelab.madoi.core.message.info.RoomInfo;

public class EnterRoomAllowed extends Message{
	public EnterRoomAllowed() {
	}
	public EnterRoomAllowed(
			RoomInfo room,
			PeerInfo selfPeer, List<PeerInfo> otherPeers, List<Object> histories) {
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
	public List<Object> getHistories() {
		return histories;
	}
	public void setHistories(List<Object> histories) {
		this.histories = histories;
	}

	private RoomInfo room;
	private PeerInfo selfPeer;
	private List<PeerInfo> otherPeers = new ArrayList<>();
	private List<Object> histories = new ArrayList<>();
}
