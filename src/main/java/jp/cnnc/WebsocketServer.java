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
package jp.cnnc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import jp.cnnc.service.DefaultRoom;
import jp.cnnc.session.WebsocketSession;
import jp.cnnc.storage.NullStorage;

@ServerEndpoint("/rooms/{roomId}")
public class WebsocketServer {
	public WebsocketServer() {
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
			synchronized(roomTtls) {
				Iterator<Map.Entry<String, Long>> it = roomTtls.entrySet().iterator();
				long cur = System.currentTimeMillis();
				while(it.hasNext()) {
					Map.Entry<String, Long> e = it.next();
					if(cur > e.getValue()) {
						it.remove();
						rooms.remove(e.getKey());
					}
				}
			}
		}, 10, 10, TimeUnit.SECONDS);
	}

	@OnOpen
	public void onOpen(Session session, @PathParam("roomId") String roomId) {
		getRoom(roomId).onPeerArrive(new WebsocketSession(session));
	}

	@OnClose
	public void onClose(Session session, @PathParam("roomId") String roomId) {
		long ttl = getRoom(roomId).onPeerClose(session.getId());
		if(ttl == 0) {
			rooms.remove(roomId).onRoomEnded();
		} else if(ttl > 0) {
			roomTtls.put(roomId, System.currentTimeMillis() + ttl);
		}
	}

	@OnMessage(maxMessageSize = 8192*1024)
	public void onMessage(
			Session session,
			@PathParam("serviceId") String serviceId,
			@PathParam("roomId") String roomId,
			String message) {
		getRoom(roomId).onPeerMessage(session.getId(), message);
	}

	protected Room getRoom(String roomId){
		return rooms.computeIfAbsent(roomId, this::newRoom);
	}

	protected Room newRoom(String roomId){
		Room r = new DefaultRoom(roomId, new NullStorage());
		r.onRoomStarted();
		return r;
	}

	private Map<String, Long> roomTtls = new HashMap<>();
	private static Map<String, Room> rooms = new ConcurrentHashMap<>();
}
