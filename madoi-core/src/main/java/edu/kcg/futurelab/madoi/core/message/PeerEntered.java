package edu.kcg.futurelab.madoi.core.message;

import edu.kcg.futurelab.madoi.core.message.info.PeerInfo;

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
