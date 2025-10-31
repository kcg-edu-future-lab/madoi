package edu.kcg.futurelab.madoi.core.message.config;

import edu.kcg.futurelab.madoi.core.message.Message;

public class FunctionConfig extends Message{
	public FunctionConfig() {
	}
	public FunctionConfig(DistributedConfig distributed, ChangeStateConfig changeState) {
		this.distributed = distributed;
		this.changeState = changeState;
	}
	public FunctionConfig(DistributedConfig share) {
		this.distributed = share;
	}
	public DistributedConfig getDistributed() {
		return distributed;
	}
	public void setDistributed(DistributedConfig distributed) {
		this.distributed = distributed;
	}
	public ChangeStateConfig getChangeState() {
		return changeState;
	}
	public void setChangeState(ChangeStateConfig changeState) {
		this.changeState = changeState;
	}

	private DistributedConfig distributed;
	private ChangeStateConfig changeState;
}
