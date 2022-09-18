package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.Message;

public class ObjectState extends Message {
	public ObjectState() {
	}
	public ObjectState(int objId, String state, int revision) {
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
