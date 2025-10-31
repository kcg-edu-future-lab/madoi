package edu.kcg.futurelab.madoi.core.message.config;

import edu.kcg.futurelab.madoi.core.message.Message;

public class MethodConfig extends Message{
	public MethodConfig() {
	}
	public MethodConfig(DistributedConfig distributed, ChangeStateConfig changeState) {
		this.distributed = distributed;
		this.changeState = changeState;
	}
	public MethodConfig(DistributedConfig distributed) {
		this.distributed = distributed;
	}
	public MethodConfig(GetStateConfig getState) {
		this.getState = getState;
	}
	public MethodConfig(SetStateConfig setState) {
		this.setState = setState;
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

	private DistributedConfig distributed;
	private ChangeStateConfig changeState;
	private GetStateConfig getState;
	private SetStateConfig setState;
}
