package jp.cnnc.madoi.core.message.config;

public class GetStateConfig {
	public GetStateConfig() {
	}
	public GetStateConfig(Integer maxInterval, Integer maxUpdates) {
		this.maxInterval = maxInterval;
		this.maxUpdates = maxUpdates;
	}
	public Integer getMaxInterval() {
		return maxInterval;
	}
	public void setMaxInterval(Integer maxInterval) {
		this.maxInterval = maxInterval;
	}
	public Integer getMaxUpdates() {
		return maxUpdates;
	}
	public void setMaxUpdates(Integer maxUpdates) {
		this.maxUpdates = maxUpdates;
	}

	private Integer maxInterval;
	private Integer maxUpdates;
}
