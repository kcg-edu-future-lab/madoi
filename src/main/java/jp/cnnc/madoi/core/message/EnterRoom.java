package jp.cnnc.madoi.core.message;

import java.util.ArrayList;
import java.util.List;

import jp.cnnc.madoi.core.Message;

public class EnterRoom extends Message{
	public static class SelfPeer{
		public SelfPeer() {
		}
		public SelfPeer(String id, int order) {
			this.id = id;
			this.order = order;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public int getOrder() {
			return order;
		}
		public void setOrder(int order) {
			this.order = order;
		}

		private String id;
		private int order;
	}

	public EnterRoom() {
	}
	public EnterRoom(SelfPeer self, List<PeerInfo> peers, List<Message> histories) {
		this.self = self;
		this.peers = peers;
		this.histories = histories;
	}

	public SelfPeer getSelf() {
		return self;
	}
	public void setSelf(SelfPeer self) {
		this.self = self;
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

	private SelfPeer self;
	private List<PeerInfo> peers = new ArrayList<>();
	private List<Message> histories = new ArrayList<>();
}
