package jp.cnnc.madoi.core.message.definition;

import jp.cnnc.madoi.core.message.config.ShareConfig;

public class FunctionDefinition {
	public FunctionDefinition() {
	}
	public FunctionDefinition(int funcId, String name, ShareConfig config) {
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
	public ShareConfig getConfig() {
		return config;
	}
	public void setConfig(ShareConfig config) {
		this.config = config;
	}
	private int funcId;
	private String name;
	private ShareConfig config;
}
