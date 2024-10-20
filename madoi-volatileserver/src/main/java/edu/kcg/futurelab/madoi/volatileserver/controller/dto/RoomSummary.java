package edu.kcg.futurelab.madoi.volatileserver.controller.dto;

import java.util.Map;

import edu.kcg.futurelab.madoi.core.message.info.RoomSpec;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomSummary {
	private String id;
	private RoomSpec spec;
	private Map<String, Object> profile;
	private int peerCount;
	private int objectCount;
	private int functionCount;
	private int historyCount;
}
