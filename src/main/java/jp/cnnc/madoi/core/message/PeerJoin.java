package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.Message;

public class PeerJoin extends Message{
	public PeerJoin() {
	}

	public PeerJoin(PeerInfo peer) {
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
