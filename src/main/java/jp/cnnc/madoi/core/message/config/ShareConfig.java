package jp.cnnc.madoi.core.message.config;

public class ShareConfig{
	public enum SharingType{
		beforeExec, afterExec
	}

	public ShareConfig() {
	}
	public ShareConfig(SharingType type, int maxLog, String[] allowedTo, UpdateConfig update) {
		this.type = type;
		this.maxLog = maxLog;
		this.allowedTo = allowedTo;
		this.update = update;
	}
	public ShareConfig(SharingType type, int maxLog) {
		this.type = type;
		this.maxLog = maxLog;
	}
	public SharingType getType() {
		return type;
	}
	public void setType(SharingType type) {
		this.type = type;
	}
	public int getMaxLog() {
		return maxLog;
	}
	public void setMaxLog(int maxLog) {
		this.maxLog = maxLog;
	}
	public String[] getAllowedTo() {
		return allowedTo;
	}
	public void setAllowedTo(String[] allowedTo) {
		this.allowedTo = allowedTo;
	}
	public UpdateConfig getUpdate() {
		return update;
	}
	public void setUpdate(UpdateConfig update) {
		this.update = update;
	}


	private SharingType type;
	private int maxLog;
	private String[] allowedTo;
	private UpdateConfig update;
}
