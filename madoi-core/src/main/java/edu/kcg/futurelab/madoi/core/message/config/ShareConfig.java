package edu.kcg.futurelab.madoi.core.message.config;

public class ShareConfig{
	public enum SharingType{
		beforeExec, afterExec
	}

	public ShareConfig() {
	}
	public ShareConfig(SharingType type) {
		this.type = type;
	}
	public SharingType getType() {
		return type;
	}
	public void setType(SharingType type) {
		this.type = type;
	}


	private SharingType type;
}
