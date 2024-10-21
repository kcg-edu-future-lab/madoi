package edu.kcg.futurelab.madoi.volatileserver.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kcg.futurelab.madoi.core.message.InvokeFunction;
import edu.kcg.futurelab.madoi.core.message.InvokeMethod;
import edu.kcg.futurelab.madoi.core.message.UpdateObjectState;
import edu.kcg.futurelab.madoi.core.message.UserMessage;
import edu.kcg.futurelab.madoi.volatileserver.controller.Util;
import edu.kcg.futurelab.madoi.volatileserver.controller.dto.History;
import edu.kcg.futurelab.madoi.volatileserver.controller.dto.RoomDetail;
import edu.kcg.futurelab.madoi.volatileserver.controller.dto.RoomSummary;
import edu.kcg.futurelab.madoi.volatileserver.websocket.Server;
import lombok.AllArgsConstructor;
import lombok.Data;

@Service
public class RoomService {
	public Collection<RoomSummary> getRoomSummaries() {
		var rm = Server.instance().getRoomManager();
		try(var s = rm.getRooms().stream()){
			return s.map(Util::summary).toList();
		}
	}

	public RoomDetail getRoomDetail(String roomId) {
		var room = Server.instance().getRoomManager().getRoom(roomId);
		if(room == null) return null;
		return Util.detail(room);
	}

	@Data
	@AllArgsConstructor
	public static class SearchHistoryResult{
		private RoomSummary room;
		private int lastItemIndexAtCurrentPage;
		private int totalItemCount;
		private int maxPage;
		private List<History> result;
	}

	public SearchHistoryResult searchRoomHistory(String roomId, int page, int itemsPerPage) {
		if(itemsPerPage <= 0) throw new IllegalArgumentException("itemsPerPage must be >= 0");
		var room = Server.instance().getRoomManager().getRoom(roomId);
		if(room == null) return null;
		var histories = room.getMessageHistories();
		var n = histories.size();
		var lastItemIndexAtCurrentPage = Math.min((page + 1) * itemsPerPage - 1, n - 1);
		var totalItemCount = n;
		var maxPage = n / itemsPerPage + (n % itemsPerPage != 0 ? 1 : 0);

		var first = itemsPerPage * page;
		var items = histories.subList(
					Math.min(first, n), Math.min(first + itemsPerPage, n));

		var result = new ArrayList<History>();
		for(var h : items) {
			switch(h.getMessageType()) {
				case "InvokeFunction":{
					var m = (InvokeFunction)h.getMessage();
					var fri = room.getFunctionRuntimeInfos().get(m.getFuncId());
					result.add(new History(
							h.getReceived(), History.Type.InvokeFunction,
							h.getSender(),
							fri.getDefinition().getName(),
							toString(m.getArgs())));
					break;
				}
				case "UpdateObjectState":{
					var m = (UpdateObjectState)h.getMessage();
					var ori = room.getObjectRuntimeInfos().get(m.getObjId());
					result.add(new History(
							h.getReceived(), History.Type.UpdateObjectState,
							h.getSender(),
							String.format("%s(%d)",
									ori.getDefinition().getClassName(),
									m.getObjRevision()),
							toString(m.getState())));
					break;
				}
				case "InvokeMethod":{
					var m = (InvokeMethod)h.getMessage();
					var ori = room.getObjectRuntimeInfos().get(m.getObjId());
					result.add(new History(
							h.getReceived(), History.Type.InvokeMethod,
							h.getSender(),
							String.format("%s(%d).%s",
									ori.getDefinition().getClassName(),
									m.getObjRevision(),
									ori.getMethods().get(m.getMethodId()).getDefinition().getName()),
							toString(m.getArgs())));
					break;
				}
				default:{
					String type = null;
					Object content = null;
					if(h.getMessage() instanceof UserMessage m) {
						type = m.getType();
						content = m.getContent();
					} else if(h.getMessage() instanceof Map m){
						type = m.get("type").toString();
						content = m.get("content");
					}
					result.add(new History(
							h.getReceived(), History.Type.UserMessage, h.getSender(),
							type,
							toString(content)));
					break;
				}
			}
		}
		return new SearchHistoryResult(
				Util.summary(room),
				lastItemIndexAtCurrentPage,
				totalItemCount,
				maxPage,
				result);
	}

	private String toString(Object value) {
		try {
			return om.writeValueAsString(value);
		} catch(JsonProcessingException e) {
			e.printStackTrace();
			return value.toString();
		}
	}

	private ObjectMapper om = new ObjectMapper();
}
