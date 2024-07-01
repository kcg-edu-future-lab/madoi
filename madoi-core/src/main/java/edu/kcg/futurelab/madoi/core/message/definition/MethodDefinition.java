package edu.kcg.futurelab.madoi.core.message.definition;

import edu.kcg.futurelab.madoi.core.message.config.MethodConfig;
import edu.kcg.futurelab.madoi.core.message.config.ShareConfig;

public class MethodDefinition {
	public MethodDefinition() {
	}
	public MethodDefinition(int methodId, String name, MethodConfig config) {
		this.methodId = methodId;
		this.name = name;
		this.config = config;
	}
	public MethodDefinition(int methodId, String name, ShareConfig config) {
		this.methodId = methodId;
		this.name = name;
		this.config = new MethodConfig(config);
	}
	public int getMethodId() {
		return methodId;
	}
	public void setMethodId(int methodId) {
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
	private int methodId;
	private String name;
	private MethodConfig config;
}
