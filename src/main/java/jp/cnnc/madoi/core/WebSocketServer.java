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

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jp.cnnc.madoi.core.room.RoomManager;
import jp.cnnc.madoi.core.room.manager.OnMemoryRoomManager;
import jp.cnnc.madoi.core.session.WebsocketSessionPeer;
import jp.go.nict.langrid.commons.lang.StringUtil;

@ServerEndpoint("/rooms/{roomId}")
public class WebSocketServer {
	@OnOpen
	public void onOpen(Session session, @PathParam("roomId") String roomId) {
		try {
			String key = StringUtil.join(
				session.getRequestParameterMap().getOrDefault("key", Arrays.asList())
				.toArray(new String[] {}), "").trim();
			getRoomManager().onPeerOpen(key, roomId, new WebsocketSessionPeer(session));
		} catch(Error | RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@OnClose
	public void onClose(Session session, @PathParam("roomId") String roomId) {
		try {
			getRoomManager().onPeerClose(roomId, session.getId());
		} catch(Error | RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@OnError
	public void onError(Session session, Throwable cause, @PathParam("roomId") String roomId) {
		try {
			getRoomManager().onPeerError(roomId, session.getId(), cause);
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
			getRoomManager().onPeerMessage(roomId, session.getId(), message);
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
			getRoomManager().onPeerMessage(roomId, session.getId(), message);
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
		return new OnMemoryRoomManager();
	}

	private RoomManager roomManager;
}
