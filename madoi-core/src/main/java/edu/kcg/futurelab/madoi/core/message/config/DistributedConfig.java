package edu.kcg.futurelab.madoi.core.message.config;

public class DistributedConfig{
	public DistributedConfig() {
		this.serialized = true;
	}
	public DistributedConfig(boolean serialized) {
		this.serialized = serialized;
	}
	public boolean isSerialized() {
		return serialized;
	}
	public void setSerialized(boolean serialized) {
		this.serialized = serialized;
	}

	private boolean serialized;
}
