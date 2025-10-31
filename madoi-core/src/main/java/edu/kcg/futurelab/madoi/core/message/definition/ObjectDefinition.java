package edu.kcg.futurelab.madoi.core.message.definition;

import java.util.ArrayList;
import java.util.List;

import edu.kcg.futurelab.madoi.core.message.config.ClassName;

public class ObjectDefinition {
	public ObjectDefinition() {
	}
	public ObjectDefinition(int objId, String className,
			ClassName config, List<MethodDefinition> methods) {
		this.objId = objId;
		this.className = className;
		this.config = config;
		this.methods.addAll(methods);
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
	public ClassName getConfig() {
		return config;
	}
	public void setConfig(ClassName config) {
		this.config = config;
	}
	public List<MethodDefinition> getMethods() {
		return methods;
	}
	public void setMethods(List<MethodDefinition> methods) {
		this.methods = methods;
	}

	private int objId;
	private String className;
	private ClassName config;
	private List<MethodDefinition> methods = new ArrayList<>();
}
