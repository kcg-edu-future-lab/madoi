package edu.kcg.futurelab.madoi.volatileserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.kcg.futurelab.madoi.volatileserver.service.SkyWayAuthTokenService;

@RestController
public class SkyWayController {
	@GetMapping("/skyWayToken")
	public String getToken() {
		return service.generate();
	}

	@Autowired
	private SkyWayAuthTokenService service;
}
