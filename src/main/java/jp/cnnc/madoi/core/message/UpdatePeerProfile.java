package jp.cnnc.madoi.core.message;

import java.util.List;
import java.util.Map;

import jp.cnnc.madoi.core.Message;

public class UpdatePeerProfile extends Message{
	public UpdatePeerProfile() {
	}

	public UpdatePeerProfile(String peerId, Map<String, Object> updates, List<String> deletes) {
		super();
		this.peerId = peerId;
		this.updates = updates;
		this.deletes = deletes;
	}

	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public Map<String, Object> getUpdates() {
		return updates;
	}

	public void setUpdates(Map<String, Object> updates) {
		this.updates = updates;
	}

	public List<String> getDeletes() {
		return deletes;
	}

	public void setDeletes(List<String> deletes) {
		this.deletes = deletes;
	}




	private String peerId;
	private Map<String, Object> updates;
	private List<String> deletes;
}
