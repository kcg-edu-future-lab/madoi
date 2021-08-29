package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.Message;

public class ObjectState extends Message {
	public ObjectState() {
	}
	public ObjectState(int objId, String state) {
		this.objId = objId;
		this.state = state;
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

	private int objId;
	private String state;
}
