package jp.cnnc.message;

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
