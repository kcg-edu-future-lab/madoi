package edu.kcg.futurelab.madoi.core.message;

import java.util.List;
import java.util.Map;

public class UpdateRoomProfile extends Message{
	public UpdateRoomProfile() {
	}

	public UpdateRoomProfile(String sender, String roomId, Map<String, Object> updates, List<String> deletes) {
		setSender(sender);
		this.roomId = roomId;
		this.updates = updates;
		this.deletes = deletes;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
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

	private String roomId;
	private Map<String, Object> updates;
	private List<String> deletes;
}
