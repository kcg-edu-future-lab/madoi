package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.Message;

public class MethodConfig extends Message{
	public enum SharingType{
		SHARE_PROCESS, SHARE_RESULT
	}

	public MethodConfig() {
	}
	public MethodConfig(int methodIndex, int maxLog, SharingType sharingType) {
		super();
		this.methodIndex = methodIndex;
		this.maxLog = maxLog;
		this.sharingType = sharingType;
	}
	public int getMethodIndex() {
		return methodIndex;
	}
	public void setMethodIndex(int methodIndex) {
		this.methodIndex = methodIndex;
	}
	public int getMaxLog() {
		return maxLog;
	}
	public void setMaxLog(int maxLog) {
		this.maxLog = maxLog;
	}
	public SharingType getSharingType() {
		return sharingType;
	}
	public void setSharingType(SharingType sharingType) {
		this.sharingType = sharingType;
	}

	private int methodIndex;
	private int maxLog;
	private SharingType sharingType;
}
