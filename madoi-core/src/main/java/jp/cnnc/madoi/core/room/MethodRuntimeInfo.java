package jp.cnnc.madoi.core.room;

import java.util.Date;

import jp.cnnc.madoi.core.message.definition.MethodDefinition;

public class MethodRuntimeInfo {
	private MethodDefinition definition;
	private int invocationCount;
	private Date lastInvocation;
	public MethodRuntimeInfo(MethodDefinition definition) {
		this.definition = definition;
	}
	public MethodDefinition getDefinition() {
		return definition;
	}
	public void setDefinition(MethodDefinition definition) {
		this.definition = definition;
	}
	public int getInvocationCount() {
		return invocationCount;
	}
	public void setInvocationCount(int invocationCount) {
		this.invocationCount = invocationCount;
	}
	public Date getLastInvocation() {
		return lastInvocation;
	}
	public void setLastInvocation(Date lastInvocation) {
		this.lastInvocation = lastInvocation;
	}
	public void onInvoked() {
		invocationCount++;
		lastInvocation = new Date();
	}
}
