package edu.kcg.futurelab.madoi.volatileserver.controller;

import edu.kcg.futurelab.madoi.core.room.FunctionRuntimeInfo;
import edu.kcg.futurelab.madoi.core.room.MethodRuntimeInfo;
import edu.kcg.futurelab.madoi.core.room.ObjectRuntimeInfo;
import edu.kcg.futurelab.madoi.core.room.Peer;
import edu.kcg.futurelab.madoi.core.room.Room;
import edu.kcg.futurelab.madoi.volatileserver.controller.dto.FunctionDetail;
import edu.kcg.futurelab.madoi.volatileserver.controller.dto.MethodDetail;
import edu.kcg.futurelab.madoi.volatileserver.controller.dto.ObjectDetail;
import edu.kcg.futurelab.madoi.volatileserver.controller.dto.PeerDetail;
import edu.kcg.futurelab.madoi.volatileserver.controller.dto.RoomDetail;
import edu.kcg.futurelab.madoi.volatileserver.controller.dto.RoomSummary;

public class Util {
	public static RoomSummary summary(Room room) {
		return new RoomSummary(
				room.getId(), room.getSpec(), room.getProfile(),
				room.getPeers().size(), room.getObjectRuntimeInfos().size(),
				room.getFunctionRuntimeInfos().size(),
				room.getMessageHistories().size());
	}

	public static RoomDetail detail(Room room) {
		try(var ps = room.getPeers().stream();
				var fs = room.getFunctionRuntimeInfos().values().stream();
				var os = room.getObjectRuntimeInfos().values().stream()){
			return new RoomDetail(
					room.getId(), room.getSpec(), room.getProfile(),
					room.getPeers().size(), room.getObjectRuntimeInfos().size(),
					room.getFunctionRuntimeInfos().size(),
					room.getMessageHistories().size(),
					ps.map(Util::detail).toList(),
					fs.map(Util::detail).toList(),
					os.map(Util::detail).toList());
		}
	}

	public static PeerDetail detail(Peer peer) {
		return new PeerDetail(
				peer.getId(), peer.getOrder(),
				peer.getProfile());
	}

	public static ObjectDetail detail(ObjectRuntimeInfo ori) {
		try(var ms = ori.getMethods().values().stream()){
			return new ObjectDetail(
					ori.getDefinition(),
					ori.getState(),
					ori.getRevision(),
					ms.map(Util::detail).toList());
		}
	}

	public static MethodDetail detail(MethodRuntimeInfo mri) {
		return new MethodDetail(
				mri.getDefinition(),
				mri.getInvocationCount(), mri.getLastInvocation());
	}

	public static FunctionDetail detail(FunctionRuntimeInfo fri) {
		return new FunctionDetail(
				fri.getDefinition(),
				fri.getInvocationCount(), fri.getLastInvocation());
	}
}
