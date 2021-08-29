package jp.cnnc.madoi.core.message;

import java.util.ArrayList;
import java.util.List;

import jp.cnnc.madoi.core.Message;

public class ObjectInfo extends Message{
	public ObjectInfo() {
	}
	public ObjectInfo(int objId, String className, List<MethodInfo> methods) {
		this.objId = objId;
		this.className = className;
		this.methods = methods;
	}
	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public List<MethodInfo> getMethods() {
		return methods;
	}
	public void setMethods(List<MethodInfo> methods) {
		this.methods = methods;
	}

	private int objId;
	private String className;
	private List<MethodInfo> methods = new ArrayList<>();
}
