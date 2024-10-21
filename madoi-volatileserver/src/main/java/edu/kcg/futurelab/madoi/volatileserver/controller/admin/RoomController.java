package edu.kcg.futurelab.madoi.volatileserver.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import edu.kcg.futurelab.madoi.volatileserver.service.RoomService;

@Controller
@RequestMapping("/admin")
public class RoomController {
	@GetMapping("/rooms")
	public String rooms(Model model) {
		model.addAttribute("rooms", rs.getRoomSummaries());
		return "admin/rooms";
	}

	@GetMapping("/rooms/{roomId}/detail")
	public String detail(
			@PathVariable String roomId, Model model) {
		var rd = rs.getRoomDetail(roomId);
		if(rd == null) {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "Room not found");
		}
		model.addAttribute("room", rd);
		return "admin/room/detail";
	}

	@GetMapping("/rooms/{roomId}/history")
	public String history(
			@PathVariable String roomId,
			@RequestParam(defaultValue="40") int itemsPerPage,
			@RequestParam(defaultValue="0") int page,
			Model model) {
		var result = rs.searchRoomHistory(roomId, page, itemsPerPage);
		if(result == null) {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "Room not found");
		}
		model.addAttribute("room", result.getRoom());
		model.addAttribute("history", result.getResult());
		model.addAttribute("itemsPerPage", itemsPerPage);
		model.addAttribute("page", page);
		model.addAttribute("maxPage", result.getMaxPage());
		model.addAttribute("firstItemIndex", page * itemsPerPage);
		model.addAttribute("lastItemIndex", result.getLastItemIndexAtCurrentPage());
		model.addAttribute("totalItemCount", result.getTotalItemCount());
		return "admin/room/history";
	}

	@Autowired
	private RoomService rs;
}
