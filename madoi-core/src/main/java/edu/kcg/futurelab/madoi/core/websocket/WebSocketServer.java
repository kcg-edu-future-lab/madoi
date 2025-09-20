package edu.kcg.futurelab.madoi.core.websocket;

import java.io.EOFException;

import edu.kcg.futurelab.madoi.core.room.Connection;
import edu.kcg.futurelab.madoi.core.room.Peer;
import edu.kcg.futurelab.madoi.core.room.RoomManager;
import edu.kcg.futurelab.madoi.core.room.impl.DefaultRoomManager;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/rooms/{roomId}")
public class WebSocketServer {
	@OnOpen
	public void onOpen(Session session, @PathParam("roomId") String roomId) {
		try {
			var peer = getRoomManager().onPeerOpen(roomId, createConnection(session));
			session.getUserProperties().put("peer", peer);
		} catch(Error | RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@OnClose
	public void onClose(Session session, @PathParam("roomId") String roomId) {
		try {
			var peer = (Peer)session.getUserProperties().get("peer");
			getRoomManager().onPeerClose(roomId, peer);
		} catch(Error | RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@OnError
	public void onError(Session session, Throwable cause, @PathParam("roomId") String roomId) {
		if(cause instanceof EOFException) {
			// クライアントの切断で起こるエラーは無視。
			return;
		}
		try {
			var peer = (Peer)session.getUserProperties().get("peer");
			getRoomManager().onPeerError(roomId, peer, cause);
		} catch(Error | RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@OnMessage(maxMessageSize = 8192*1024)
	public void onMessage(
			Session session,
			@PathParam("roomId") String roomId,
			String message) {
		try {
			var peer = (Peer)session.getUserProperties().get("peer");
			getRoomManager().onPeerMessage(roomId, peer, message);
		} catch(Error | RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@OnMessage(maxMessageSize = 8192*1024)
	public void onMessage(
			Session session,
			@PathParam("roomId") String roomId,
			byte[] message) {
		try {
			var peer = (Peer)session.getUserProperties().get("peer");
			getRoomManager().onPeerMessage(roomId, peer, message);
		} catch(Error | RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public synchronized RoomManager getRoomManager() {
		if(roomManager == null) {
			roomManager = createRoomManager();
		}
		return roomManager;
	}

	protected RoomManager createRoomManager() {
		return new DefaultRoomManager();
	}
	
	protected Connection createConnection(Session session) {
		return new WebSocketConnection(session);
	}

	private RoomManager roomManager;
}
