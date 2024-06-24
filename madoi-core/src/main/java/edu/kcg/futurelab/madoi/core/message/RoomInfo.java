package edu.kcg.futurelab.madoi.core.message;

import java.util.Map;

public class RoomInfo {
	public RoomInfo() {
		this(null);
	}
	public RoomInfo(String id) {
		this(id, null, null);
	}
	public RoomInfo(String id, RoomSpec spec, Map<String, Object> profile) {
		this.id = id;
		this.spec = spec;
		this.profile = profile;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public RoomSpec getSpec() {
		return spec;
	}
	public void setSpec(RoomSpec spec) {
		this.spec = spec;
	}
	public Map<String, Object> getProfile() {
		return profile;
	}
	public void setProfile(Map<String, Object> profile) {
		this.profile = profile;
	}
	private String id;
	private RoomSpec spec;
	private Map<String, Object> profile;
}
