package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.Message;

public class Invocation extends Message {
	public Invocation(){
	}
	public Invocation(Integer objId, int funcId, String funcName, Object[] args){
		this.objId = objId;
		this.funcId = funcId;
		this.funcName = funcName;
		this.args = args;
	}
	public Invocation(int funcId, String funcName, Object[] args){
		this.funcId = funcId;
		this.funcName = funcName;
		this.args = args;
	}
	public Integer getObjId() {
		return objId;
	}
	public void setObjId(Integer objId) {
		this.objId = objId;
	}
	public int getFuncId() {
		return funcId;
	}
	public void setFuncId(int funcId) {
		this.funcId = funcId;
	}
	public String getFuncName() {
		return funcName;
	}
	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	private Integer objId;
	private int funcId;
	private String funcName;
	private Object[] args;
}
