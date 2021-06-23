package edu.kcg.futurelab.middleman.service.message;

public class ObjectState extends Message {
	public ObjectState() {
	}
	public ObjectState(int objectIndex, String state) {
		this.objectIndex = objectIndex;
		this.state = state;
	}
	public int getObjectIndex() {
		return objectIndex;
	}
	public void setObjectIndex(int objectIndex) {
		this.objectIndex = objectIndex;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	private int objectIndex;
	private String state;
}
