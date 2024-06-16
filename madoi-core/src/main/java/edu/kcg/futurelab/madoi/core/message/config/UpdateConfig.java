package edu.kcg.futurelab.madoi.core.message.config;

public class UpdateConfig {
	public UpdateConfig() {
	}
	public UpdateConfig(Integer freq, Integer interpolateBy, Integer reckonUntil) {
		this.freq = freq;
		this.interpolateBy = interpolateBy;
		this.reckonUntil = reckonUntil;
	}
	public Integer getFreq() {
		return freq;
	}
	public void setFreq(Integer freq) {
		this.freq = freq;
	}
	public Integer getInterpolateBy() {
		return interpolateBy;
	}
	public void setInterpolateBy(Integer interpolateBy) {
		this.interpolateBy = interpolateBy;
	}
	public Integer getReckonUntil() {
		return reckonUntil;
	}
	public void setReckonUntil(Integer reckonUntil) {
		this.reckonUntil = reckonUntil;
	}

	private Integer freq;
	private Integer interpolateBy;
	private Integer reckonUntil;
}
