package jp.cnnc.madoi.core.message;

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
