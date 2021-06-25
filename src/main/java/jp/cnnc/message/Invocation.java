package jp.cnnc.message;

public class Invocation extends Message {
	private int methodIndex;
	private int objectIndex;
	private Object[] args;

	public Invocation(){
	}
	public Invocation(int methodIndex, int objectIndex, Object[] args){
		this.methodIndex = methodIndex;
		this.objectIndex = objectIndex;
		this.args = args;
	}
	public int getMethodIndex() {
		return methodIndex;
	}
	public void setMethodIndex(int methodIndex) {
		this.methodIndex = methodIndex;
	}
	public int getObjectIndex() {
		return objectIndex;
	}
	public void setObjectIndex(int objectIndex) {
		this.objectIndex = objectIndex;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
}
