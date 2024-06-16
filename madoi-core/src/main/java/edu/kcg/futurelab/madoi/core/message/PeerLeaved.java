package edu.kcg.futurelab.madoi.core.message;

public class PeerLeaved extends Message{
	public PeerLeaved() {
	}

	public PeerLeaved(String peerId) {
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
