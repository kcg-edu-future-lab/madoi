package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.Message;

public class PeerEntered extends Message{
	public PeerEntered() {
	}

	public PeerEntered(PeerInfo peer) {
		this.peer = peer;
	}

	public PeerInfo getPeer() {
		return peer;
	}

	public void setPeer(PeerInfo peer) {
		this.peer = peer;
	}

	private PeerInfo peer;
}
