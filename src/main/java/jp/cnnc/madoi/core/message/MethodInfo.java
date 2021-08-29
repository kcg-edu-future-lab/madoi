package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.message.config.MethodConfig;
import jp.cnnc.madoi.core.message.config.ShareConfig;

public class MethodInfo {
	public MethodInfo() {
	}
	public MethodInfo(Integer funcId, String name, MethodConfig config) {
		this.funcId = funcId;
		this.name = name;
		this.config = config;
	}
	public MethodInfo(Integer funcId, String name, ShareConfig config) {
		this.funcId = funcId;
		this.name = name;
		this.config = new MethodConfig(config);
	}
	public Integer getFuncId() {
		return funcId;
	}
	public void setFuncId(Integer funcId) {
		this.funcId = funcId;
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
	private Integer funcId;
	private String name;
	private MethodConfig config;
}
