package edu.kcg.futurelab.madoi.volatileserver.controller.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeerDetail {
	private String id;
	private int order;
	private Map<String, Object> profile;
}
