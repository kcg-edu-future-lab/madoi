package jp.cnnc.madoi.core.message.definition;

import jp.cnnc.madoi.core.message.config.FunctionConfig;

public class FunctionDefinition {
	public FunctionDefinition() {
	}
	public FunctionDefinition(int funcId, String name, FunctionConfig config) {
		this.funcId = funcId;
		this.name = name;
		this.config = config;
	}
	public int getFuncId() {
		return funcId;
	}
	public void setFuncId(int funcId) {
		this.funcId = funcId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public FunctionConfig getConfig() {
		return config;
	}
	public void setConfig(FunctionConfig config) {
		this.config = config;
	}
	private int funcId;
	private String name;
	private FunctionConfig config;
}
