package edu.kcg.futurelab.madoi.volatileserver.controller.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kcg.futurelab.madoi.core.message.InvokeFunction;
import edu.kcg.futurelab.madoi.core.message.InvokeMethod;
import edu.kcg.futurelab.madoi.core.message.UpdateObjectState;
import edu.kcg.futurelab.madoi.core.message.UserMessage;
import edu.kcg.futurelab.madoi.core.room.DefaultRoom;
import edu.kcg.futurelab.madoi.core.room.Room;
import edu.kcg.futurelab.madoi.volatileserver.controller.Util;
import edu.kcg.futurelab.madoi.volatileserver.controller.dto.History;
import edu.kcg.futurelab.madoi.volatileserver.websocket.Server;
import lombok.AllArgsConstructor;
import lombok.Data;

@Controller
@RequestMapping("/admin")
public class RoomController {
	@GetMapping("/rooms")
	public String rooms(Model model) {
		var rm = Server.instance().getRoomManager();
		try(var s = rm.getRooms().stream()){
			model.addAttribute("rooms",
					s.map(Util::summary).toList());
		}
		return "admin/rooms";
	}

	@GetMapping("/rooms/{roomId}/detail")
	public String state(
			@PathVariable() String roomId, Model model) {
		var rm = Server.instance().getRoomManager();
		model.addAttribute("room", Util.detail(rm.getRoom(roomId)));
		return "admin/room/detail";
	}

	@GetMapping("/rooms/{roomId}/history")
	public String history(
			@PathVariable() String roomId,
			@RequestParam(defaultValue="40") int itemsPerPage,
			@RequestParam(defaultValue="0") int page,
			Model model) {
		var rm = Server.instance().getRoomManager().getRoom(roomId);
		model.addAttribute("room", rm);
		var histories = rm.getMessageHistories();
		var result = extract(histories, itemsPerPage, page, rm);
		model.addAttribute("history", result.getResult());
		model.addAttribute("itemsPerPage", itemsPerPage);
		model.addAttribute("page", page);
		model.addAttribute("maxPage", result.getMaxPage());
		model.addAttribute("firstItemIndex", page * itemsPerPage);
		model.addAttribute("lastItemIndex",
				Math.min((page + 1) * itemsPerPage - 1, histories.size() - 1));
		model.addAttribute("totalItemCount", histories.size());
		return "admin/room/history";
	}

	@Data
	@AllArgsConstructor
	static class HistoryExtractionResult{
		private int maxPage;
		private List<History> result;
	}

	private HistoryExtractionResult extract(
			List<DefaultRoom.History> values, int itemsPerPage, int page, Room room
			) {
		var n = values.size();
		var maxPage = n / itemsPerPage + (n % itemsPerPage != 0 ? 1 : 0);
		var first = itemsPerPage * page;
		var items = values.subList(
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

		return new HistoryExtractionResult(maxPage, result);
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
