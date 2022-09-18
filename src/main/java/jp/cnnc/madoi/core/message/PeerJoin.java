package jp.cnnc.madoi.core.message;

import java.util.Map;

import jp.cnnc.madoi.core.Message;

public class PeerJoin extends Message{
	public PeerJoin() {
	}

	public PeerJoin(String peerId, Map<String, Object> peerProfile) {
		this.peerId = peerId;
		this.peerProfile = peerProfile;
	}

	public String getPeerId() {
		return peerId;
	}
	
	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}
	public Map<String, Object> getPeerProfile() {
		return peerProfile;
	}
	public void setPeerProfile(Map<String, Object> peerProfile) {
		this.peerProfile = peerProfile;
	}

	private String peerId;
	private Map<String, Object> peerProfile;
}
