package jp.cnnc.madoi.core.message;

public class PeerInfo {
	public PeerInfo() {
	}
	public PeerInfo(String id, int order) {
		this.id = id;
		this.order = order;
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
	private String id;
	private int order;
}
