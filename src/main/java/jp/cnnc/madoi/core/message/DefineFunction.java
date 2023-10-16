package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.Message;
import jp.cnnc.madoi.core.message.config.ShareConfig;

public class DefineFunction extends Message{
	public DefineFunction() {
	}
	public DefineFunction(int funcId, String name, ShareConfig config) {
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
