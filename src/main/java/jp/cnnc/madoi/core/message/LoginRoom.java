package jp.cnnc.madoi.core.message;

import java.util.Map;

import jp.cnnc.madoi.core.Message;

public class LoginRoom extends Message{
	public LoginRoom() {
	}
	public LoginRoom(String key, Map<String, Object> roomSpec, Map<String, Object> peerProfile) {
		super();
		this.key = key;
		this.roomSpec = roomSpec;
		this.peerProfile = peerProfile;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Map<String, Object> getRoomSpec() {
		return roomSpec;
	}
	public void setRoomSpec(Map<String, Object> roomSpec) {
		this.roomSpec = roomSpec;
	}
	public Map<String, Object> getPeerProfile() {
		return peerProfile;
	}
	public void setPeerProfile(Map<String, Object> peerProfile) {
		this.peerProfile = peerProfile;
	}

	private String key;
	private Map<String, Object> roomSpec;
	private Map<String, Object> peerProfile;
}
