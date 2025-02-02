/*
 * Copyright 2017 Takao Nakaguchi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.kcg.futurelab.madoi.core.websocket;

import java.io.EOFException;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kcg.futurelab.madoi.core.message.Message;
import edu.kcg.futurelab.madoi.core.room.DefaultRoomManager;
import edu.kcg.futurelab.madoi.core.room.Connection;
import edu.kcg.futurelab.madoi.core.room.Peer;
import edu.kcg.futurelab.madoi.core.room.RoomManager;
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
			var om = new ObjectMapper();
			var peer = getRoomManager().onPeerOpen(roomId, new Connection() {
				@Override
				public void sendText(String message) throws IOException {
					session.getBasicRemote().sendText(message);
				}
				@Override
				public void send(Message message) throws IOException{
					session.getBasicRemote().sendText(om.writeValueAsString(message));
				}
				@Override
				public void close() throws IOException {
					session.close();
				}
			});
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

	private RoomManager roomManager;
}
