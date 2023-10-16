package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.Message;

public class InvokeMethodOrFunction extends Message {
	public InvokeMethodOrFunction(){
	}
	public InvokeMethodOrFunction(Integer objId, int methodId, String methodName, Object[] args){
		this.objId = objId;
		this.methodId = methodId;
		this.methodName = methodName;
		this.args = args;
	}
	public Integer getObjId() {
		return objId;
	}
	public void setObjId(Integer objId) {
		this.objId = objId;
	}
	public int getObjRevision() {
		return objRevision;
	}
	public void setObjRevision(int objRevision) {
		this.objRevision = objRevision;
	}
	public int getMethodId() {
		return methodId;
	}
	public void setMethodId(int methodId) {
		this.methodId = methodId;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	private Integer objId;
	private int objRevision;
	private int methodId;
	private String methodName;
	private Object[] args;
}
