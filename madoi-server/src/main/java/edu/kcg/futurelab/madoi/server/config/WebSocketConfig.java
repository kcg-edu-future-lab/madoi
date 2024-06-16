package edu.kcg.futurelab.madoi.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
public class WebSocketConfig {
	@Bean
	ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	@Bean
	ServletServerContainerFactoryBean createWebSocketContainer() {
		var container = new ServletServerContainerFactoryBean();
		container.setMaxTextMessageBufferSize(10 * 1024 * 1024);
		container.setMaxBinaryMessageBufferSize(10 * 1024 * 1024);
		return container;
	}

	@Value("${madoi.websocket.maxTextMessageBufferSize:10485760}")
	private int maxTextMessageBufferSize;
	@Value("${madoi.websocket.maxBinaryMessageBufferSize:10485760}")
	private int maxBinaryMessageBufferSize;
}
