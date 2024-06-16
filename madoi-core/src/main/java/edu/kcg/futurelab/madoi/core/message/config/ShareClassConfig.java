package edu.kcg.futurelab.madoi.core.message.config;

public class ShareClassConfig{
	public ShareClassConfig() {
	}
	public ShareClassConfig(String className) {
			this.className = className;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}

	private String className;
}
