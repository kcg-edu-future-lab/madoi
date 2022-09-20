package jp.cnnc.madoi.core.message;

import java.util.ArrayList;
import java.util.List;

import jp.cnnc.madoi.core.Message;

public class EnterRoom extends Message{
	public EnterRoom() {
	}

	public EnterRoom(String selfPeerId, int selfPeerOrder,
			List<PeerInfo> peers, List<Message> histories) {
		this.selfPeerId = selfPeerId;
		this.selfPeerOrder = selfPeerOrder;
		this.peers = peers;
		this.histories = histories;
	}

	public String getSelfPeerId() {
		return selfPeerId;
	}

	public void setSelfPeerId(String peerId) {
		this.selfPeerId = peerId;
	}
	public int getSelfPeerOrder() {
		return selfPeerOrder;
	}
	public void setSelfPeerOrder(int selfPeerOrder) {
		this.selfPeerOrder = selfPeerOrder;
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

	private String selfPeerId;
	private int selfPeerOrder;
	private List<PeerInfo> peers = new ArrayList<>();
	private List<Message> histories = new ArrayList<>();
}
