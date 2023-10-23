package jp.cnnc.madoi.core.message;

import java.util.ArrayList;
import java.util.List;

import jp.cnnc.madoi.core.Message;
import jp.cnnc.madoi.core.message.config.MethodConfig;
import jp.cnnc.madoi.core.message.config.ShareConfig;

public class DefineObject extends Message{
	public static class MethodDefinition {
		public MethodDefinition() {
		}
		public MethodDefinition(Integer methodId, String name, MethodConfig config) {
			this.methodId = methodId;
			this.name = name;
			this.config = config;
		}
		public MethodDefinition(Integer methodId, String name, ShareConfig config) {
			this.methodId = methodId;
			this.name = name;
			this.config = new MethodConfig(config);
		}
		public Integer getMethodId() {
			return methodId;
		}
		public void setMethodId(Integer methodId) {
			this.methodId = methodId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public MethodConfig getConfig() {
			return config;
		}
		public void setConfig(MethodConfig config) {
			this.config = config;
		}
		private Integer methodId;
		private String name;
		private MethodConfig config;
	}

	public DefineObject() {
	}
	public DefineObject(int objId, String className, List<MethodDefinition> methods) {
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
	public List<MethodDefinition> getMethods() {
		return methods;
	}
	public void setMethods(List<MethodDefinition> methods) {
		this.methods = methods;
	}

	private int objId;
	private String className;
	private List<MethodDefinition> methods = new ArrayList<>();
}
