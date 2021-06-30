package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.Message;

public class PeerLeave extends Message{
	public PeerLeave() {
	}

	public PeerLeave(String peerId) {
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
