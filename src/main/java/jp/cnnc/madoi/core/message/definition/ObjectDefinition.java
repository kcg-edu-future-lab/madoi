package jp.cnnc.madoi.core.message.definition;

import java.util.ArrayList;
import java.util.List;

public class ObjectDefinition {
	public ObjectDefinition() {
	}
	public ObjectDefinition(int objId, String name, List<MethodDefinition> methods) {
		this.objId = objId;
		this.name = name;
		this.methods = methods;
	}
	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<MethodDefinition> getMethods() {
		return methods;
	}
	public void setMethods(List<MethodDefinition> methods) {
		this.methods = methods;
	}

	private int objId;
	private String name;
	private List<MethodDefinition> methods = new ArrayList<>();
}
