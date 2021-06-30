package jp.cnnc.madoi.core.message;

import java.util.ArrayList;
import java.util.List;

import jp.cnnc.madoi.core.Message;

public class ObjectConfig extends Message{
	public ObjectConfig() {
	}
	public ObjectConfig(int objectIndex, List<Integer> methodIndices) {
		this.objectIndex = objectIndex;
		this.methodIndices = methodIndices;
	}
	public int getObjectIndex() {
		return objectIndex;
	}
	public void setObjectIndex(int objectIndex) {
		this.objectIndex = objectIndex;
	}
	public List<Integer> getMethodIndices() {
		return methodIndices;
	}
	public void setMethodIndices(List<Integer> methodIndices) {
		this.methodIndices = methodIndices;
	}

	private int objectIndex;
	private List<Integer> methodIndices = new ArrayList<>();
}