package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.Message;

public class PeerJoin extends Message{
	public PeerJoin() {
	}

	public PeerJoin(String peerId) {
		this.peerId = peerId;
	}

	public String getPeerId() {
		return peerId;
	}
	
	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	private String peerId;
}
