package jp.cnnc.madoi.core.message.config;

import jp.cnnc.madoi.core.Message;

public class MethodConfig extends Message{
	public MethodConfig() {
	}
	public MethodConfig(ShareConfig share) {
		this.share = share;
	}
	public MethodConfig(GetStateConfig getState) {
		this.getState = getState;
	}
	public MethodConfig(SetStateConfig setState) {
		this.setState = setState;
	}
	public ShareConfig getShare() {
		return share;
	}
	public void setShare(ShareConfig share) {
		this.share = share;
	}
	public GetStateConfig getGetState() {
		return getState;
	}
	public void setGetState(GetStateConfig getState) {
		this.getState = getState;
	}
	public SetStateConfig getSetState() {
		return setState;
	}
	public void setSetState(SetStateConfig setState) {
		this.setState = setState;
	}

	private ShareConfig share;
	private GetStateConfig getState;
	private SetStateConfig setState;
}
