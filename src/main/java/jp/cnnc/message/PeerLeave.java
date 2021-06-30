package jp.cnnc.message;

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
