package edu.kcg.futurelab.madoi.core.message;

/**
 * メソッド実行を表すメッセージ。FunctionはobjIdがnullのメソッドとして扱う。
 */
public class InvokeMethod extends Message {
	public InvokeMethod(){
	}
	public InvokeMethod(int objId, int objRevision, int methodId, Object[] args){
		this.objId = objId;
		this.objRevision = objRevision;
		this.methodId = methodId;
		this.args = args;
	}
	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
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
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	private int objId;
	private int objRevision;
	private int methodId;
	private Object[] args;
}
