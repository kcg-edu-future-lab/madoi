package edu.kcg.futurelab.madoi.volatileserver;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties("madoi")
@Data
public class ApplicationProperties {
	private String[] authTokens;
	private String[] allowedOrigins;
}
