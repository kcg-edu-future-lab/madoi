package jp.cnnc.madoi.core.room.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import jp.cnnc.madoi.core.Room;
import jp.cnnc.madoi.core.room.DefaultRoom;
import jp.cnnc.madoi.core.room.RoomManager;
import jp.cnnc.madoi.core.room.eventlogger.PrintRoomEventLogger;
import jp.cnnc.madoi.core.session.WebsocketSessionPeer;

public class OnMemoryRoomManager implements RoomManager{
	public OnMemoryRoomManager() {
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
			synchronized(roomTtls) {
				Iterator<Map.Entry<String, Long>> it = roomTtls.entrySet().iterator();
				long cur = System.currentTimeMillis();
				while(it.hasNext()) {
					Map.Entry<String, Long> e = it.next();
					if(cur > e.getValue()) {
						rooms.remove(e.getKey());
						it.remove();
					}
				}
			}
		}, 10, 10, TimeUnit.SECONDS);
	}

	@Override
	public void onPeerOpen(String key, String roomId, WebsocketSessionPeer sessionPeer) {
		synchronized(roomTtls) {
			Room r = getRoom(roomId);
			r.onPeerArrive(sessionPeer);
			roomTtls.remove(roomId);
		}
	}

	@Override
	public void onPeerError(String roomId, String peerId, Throwable cause) {
		getRoom(roomId).onPeerError(peerId, cause);
	}

	@Override
	public void onPeerClose(String roomId, String peerId) {
		Room r = getRoom(roomId);
		r.onPeerLeave(peerId);
		if(r.getPeerCount() == 0) {
			roomTtls.put(roomId, System.currentTimeMillis() + TTL_ROOM);
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

	@Override
	public Room getRoom(String roomId){
		return rooms.computeIfAbsent(roomId, this::newRoom);
	}

	@Override
	public Collection<Room> getRooms() {
		return rooms.values();
	}

	protected Room newRoom(String roomId){
		return new DefaultRoom(roomId, new PrintRoomEventLogger());
	}

	private Map<String, Long> roomTtls = new HashMap<>();
	private static Map<String, Room> rooms = new ConcurrentHashMap<>();
	private static final int TTL_ROOM = 10 * 60 * 1000; // 空になってから10分で削除
}
