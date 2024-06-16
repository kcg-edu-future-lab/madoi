package jp.cnnc.madoi.core.message;

public class UpdateObjectState extends Message {
	public UpdateObjectState() {
	}
	public UpdateObjectState(int objId, String state, int revision) {
		this.objId = objId;
		this.state = state;
		this.revision = revision;
	}
	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getRevision() {
		return revision;
	}
	public void setRevision(int revision) {
		this.revision = revision;
	}

	private int objId;
	private String state;
	private int revision;
}
