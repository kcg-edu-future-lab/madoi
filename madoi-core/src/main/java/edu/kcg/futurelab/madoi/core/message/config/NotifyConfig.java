package edu.kcg.futurelab.madoi.core.message.config;

public class NotifyConfig{
	public enum NotifyType{
		beforeExec, afterExec
	}

	public NotifyConfig() {
	}
	public NotifyConfig(NotifyType type) {
		this.type = type;
	}
	public NotifyType getType() {
		return type;
	}
	public void setType(NotifyType type) {
		this.type = type;
	}

	private NotifyType type;
}
