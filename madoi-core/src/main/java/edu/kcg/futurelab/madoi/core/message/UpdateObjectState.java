package edu.kcg.futurelab.madoi.core.message;

public class UpdateObjectState extends Message {
	public UpdateObjectState() {
	}
	public UpdateObjectState(int objId, Object state, int objRevision) {
		this.objId = objId;
		this.state = state;
		this.objRevision = objRevision;
	}
	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public Object getState() {
		return state;
	}
	public void setState(Object state) {
		this.state = state;
	}
	public int getObjRevision() {
		return objRevision;
	}
	public void setObjRevision(int objRevision) {
		this.objRevision = objRevision;
	}

	private int objId;
	private Object state;
	private int objRevision;
}
