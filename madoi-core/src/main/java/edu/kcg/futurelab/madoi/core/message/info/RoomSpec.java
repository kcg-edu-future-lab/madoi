package edu.kcg.futurelab.madoi.core.message.info;

import java.util.HashMap;

public class RoomSpec {
	public RoomSpec() {
	}
	public RoomSpec(int maxLog) {
		this.maxLog = maxLog;
	}

	public int getMaxLog() {
		return maxLog;
	}
	public void setMaxLog(int maxLog) {
		this.maxLog = maxLog;
	}

	@SuppressWarnings("serial")
	@Override
	public String toString() {
		return new HashMap<String, Object>(){{
			put("maxLog", maxLog);
		}}.toString();
	}

	private int maxLog = 1000;
}
