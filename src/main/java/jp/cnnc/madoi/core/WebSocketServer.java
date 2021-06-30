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
package jp.cnnc.madoi.core;

import java.util.Arrays;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import jp.cnnc.madoi.core.room.RoomManager;
import jp.cnnc.madoi.core.room.manager.OnMemoryRoomManager;
import jp.cnnc.madoi.core.session.WebsocketSession;
import jp.go.nict.langrid.commons.lang.StringUtil;

@ServerEndpoint("/rooms/{roomId}")
public class WebSocketServer {
	public WebSocketServer() {
	}

	@OnOpen
	public void onOpen(Session session, @PathParam("roomId") String roomId) {
		String key = StringUtil.join(
				session.getRequestParameterMap().getOrDefault("key", Arrays.asList())
				.toArray(new String[] {}), "").trim();
		getRoomManager().onPeerOpen(key, roomId, new WebsocketSession(session));
	}

	@OnClose
	public void onClose(Session session, @PathParam("roomId") String roomId) {
		getRoomManager().onPeerClose(roomId, session.getId());
	}


	@OnMessage(maxMessageSize = 8192*1024)
	public void onMessage(
			Session session,
			@PathParam("serviceId") String serviceId,
			@PathParam("roomId") String roomId,
			String message) {
		getRoomManager().onPeerMessage(roomId, session.getId(), message);
	}

	@OnMessage(maxMessageSize = 8192*1024)
	public void onMessage(
			Session session,
			@PathParam("serviceId") String serviceId,
			@PathParam("roomId") String roomId,
			byte[] message) {
		getRoomManager().onPeerMessage(roomId, session.getId(), message);
	}

	protected synchronized RoomManager getRoomManager() {
		if(roomManager == null) {
			roomManager = createRoomManager();
		}
		return roomManager;
	}

	protected RoomManager createRoomManager() {
		return new OnMemoryRoomManager();
	}

	private RoomManager roomManager;
}
