package edu.kcg.futurelab.madoi.core.message.info;

import java.util.Map;

public class PeerInfo {
	public PeerInfo() {
	}
	public PeerInfo(String id, int order, Map<String, Object> profile) {
		this.id = id;
		this.order = order;
		this.profile = profile;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public Map<String, Object> getProfile() {
		return profile;
	}
	public void setProfile(Map<String, Object> profile) {
		this.profile = profile;
	}
	private String id;
	private int order;
	private Map<String, Object> profile;
}
