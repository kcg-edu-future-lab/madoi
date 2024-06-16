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

import java.io.IOException;
import java.util.Arrays;

import edu.kcg.futurelab.madoi.core.message.Message;
import edu.kcg.futurelab.madoi.core.room.DefaultRoomManager;
import edu.kcg.futurelab.madoi.core.room.MessageSender;
import edu.kcg.futurelab.madoi.core.room.Peer;
import edu.kcg.futurelab.madoi.core.room.RoomManager;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jp.go.nict.langrid.commons.lang.StringUtil;
import jp.go.nict.langrid.repackaged.net.arnx.jsonic.JSON;

@ServerEndpoint("/rooms/{roomId}")
public class WebSocketServer {
	@OnOpen
	public void onOpen(Session session, @PathParam("roomId") String roomId) {
		try {
			var key = StringUtil.join(
				session.getRequestParameterMap().getOrDefault("apikey", Arrays.asList())
				.toArray(new String[] {}), "").trim();
			System.out.println("key: " + key);
			var peer = getRoomManager().onPeerOpen(roomId, new MessageSender() {
				@Override
				public void sendText(String message) throws IOException {
					session.getBasicRemote().sendText(message);
				}
				@Override
				public void send(Message message) throws IOException{
					session.getBasicRemote().sendText(JSON.encode(message));
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
