package edu.kcg.futurelab.madoi.volatileserver.controller.dto;

import java.util.Collection;
import java.util.Map;

import edu.kcg.futurelab.madoi.core.message.info.RoomSpec;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RoomDetail extends RoomSummary{
	private Collection<PeerDetail> peers;
	private Collection<FunctionDetail> functions;
	private Collection<ObjectDetail> objects;
	public RoomDetail(String roomId, RoomSpec spec, Map<String, Object> profile,
			int peerCount, int objectCount, int functionCount, int historyCount,
			Collection<PeerDetail> peers, Collection<FunctionDetail> functions,
			Collection<ObjectDetail> objects) {
		super(roomId, spec, profile, peerCount, objectCount, functionCount, historyCount);
		this.peers = peers;
		this.functions = functions;
		this.objects = objects;
	}
}
