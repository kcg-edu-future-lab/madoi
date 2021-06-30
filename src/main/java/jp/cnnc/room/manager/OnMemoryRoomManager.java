package jp.cnnc.room.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import jp.cnnc.Room;
import jp.cnnc.room.DefaultRoom;
import jp.cnnc.room.RoomManager;
import jp.cnnc.room.eventlogger.PrintRoomEventLogger;
import jp.cnnc.session.WebsocketSession;

public class OnMemoryRoomManager implements RoomManager{
	public OnMemoryRoomManager() {
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
	
	@Override
	public void onPeerOpen(String key, String roomId, WebsocketSession sessionPeer) {
		getRoom(roomId).onPeerArrive(sessionPeer);
	}
	
	@Override
	public void onPeerClose(String roomId, String peerId) {
		long ttl = getRoom(roomId).onPeerClose(peerId);
		if(ttl == 0) {
			rooms.remove(roomId).onRoomEnded();
		} else if(ttl > 0) {
			roomTtls.put(roomId, System.currentTimeMillis() + ttl);
		}
	}
	
	@Override
	public void onPeerMessage(String roomId, String peerId, String message) {
		getRoom(roomId).onPeerMessage(peerId, message);
	}

	@Override
	public void onPeerMessage(String roomId, String peerId, byte[] message) {
		getRoom(roomId).onPeerMessage(peerId, message);
	}

	public Room getRoom(String roomId){
		return rooms.computeIfAbsent(roomId, this::newRoom);
	}

	protected Room newRoom(String roomId){
		Room r = new DefaultRoom(roomId, new PrintRoomEventLogger());
		r.onRoomStarted();
		return r;
	}


	private Map<String, Long> roomTtls = new HashMap<>();
	private static Map<String, Room> rooms = new ConcurrentHashMap<>();
}
