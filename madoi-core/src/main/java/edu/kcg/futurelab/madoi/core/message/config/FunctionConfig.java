package edu.kcg.futurelab.madoi.core.message.config;

import edu.kcg.futurelab.madoi.core.message.Message;

public class FunctionConfig extends Message{
	public FunctionConfig() {
	}
	public FunctionConfig(ShareConfig share) {
		this.share = share;
	}
	public ShareConfig getShare() {
		return share;
	}
	public void setShare(ShareConfig share) {
		this.share = share;
	}

	private ShareConfig share;
}
