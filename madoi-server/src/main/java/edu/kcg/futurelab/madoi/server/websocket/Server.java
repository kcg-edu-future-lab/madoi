package edu.kcg.futurelab.madoi.server.websocket;

import org.springframework.stereotype.Component;

import edu.kcg.futurelab.madoi.core.websocket.WebSocketServer;
import jakarta.websocket.server.ServerEndpoint;

@Component
@ServerEndpoint("/rooms/{roomId}")
public class Server extends WebSocketServer {
}
