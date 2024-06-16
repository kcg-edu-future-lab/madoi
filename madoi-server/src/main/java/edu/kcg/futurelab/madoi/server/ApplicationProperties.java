package edu.kcg.futurelab.madoi.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("madoi")
public class ApplicationProperties {
	public String[] getAllowedOrigins() {
		return allowedOrigins;
	}
	public void setAllowedOrigins(String[] allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}
	public String[] getApiKeys() {
		return apiKeys;
	}
	public void setApiKeys(String[] apiKeys) {
		this.apiKeys = apiKeys;
	}
	private String[] apiKeys;
	private String[] allowedOrigins;
}
