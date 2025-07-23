package edu.kcg.futurelab.madoi.volatileserver.service;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class SkyWayAuthTokenService {
	@SuppressWarnings("serial")
	public String generate() {
		var jti = UUID.randomUUID().toString();
		var iat = new Date().getTime() / 1000;
		var exp = iat + 60 * 60 * 24; // 1 day
		return JWT.create()
			.withPayload("""
				{
					"jti": "%s",
					"iat": %d,
					"exp": %d,
					"version": 3,
					"scope": {
						"appId": "%s",
						"rooms": [{
							"name": "*",
							"methods": ["create", "close", "updateMetadata"],
							"member": {
								"name": "*",
								"methods": ["publish", "subscribe", "updateMetadata"]
							}
						}]
					}
				}
				""".formatted(jti, iat, exp, appId))
			.sign(Algorithm.HMAC256(secret));
	}

	@Value("${skyWay.appId}")
	private String appId;
	@Value("${skyWay.secret}")
	private String secret;
}
