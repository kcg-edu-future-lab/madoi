package jp.cnnc.madoi.core.message;

import java.util.Map;

public class RoomInfo {
	public RoomInfo() {
		this(null);
	}
	public RoomInfo(String id) {
		this(id, null);
	}
	public RoomInfo(String id, Map<String, Object> profile) {
		this.id = id;
		this.profile = profile;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Map<String, Object> getProfile() {
		return profile;
	}
	public void setProfile(Map<String, Object> profile) {
		this.profile = profile;
	}
	private String id;
	private Map<String, Object> profile;
}
