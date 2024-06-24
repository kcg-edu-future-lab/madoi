package edu.kcg.futurelab.madoi.core.room;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import edu.kcg.futurelab.madoi.core.room.logger.PrintRoomEventLogger;

public class DefaultRoomManager implements RoomManager{
	public DefaultRoomManager() {
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
			synchronized(roomTtls) {
				var it = roomTtls.entrySet().iterator();
				var cur = System.currentTimeMillis();
				while(it.hasNext()) {
					var e = it.next();
					if(cur > e.getValue()) {
						rooms.remove(e.getKey());
						it.remove();
					}
				}
			}
		}, 10, 10, TimeUnit.SECONDS);
	}

	@Override
	public Peer onPeerOpen(String roomId, MessageSender sender) {
		synchronized(roomTtls) {
			var r = getRoom(roomId);
			var p = newPeer(sender);
			r.onPeerArrive(p);
			roomTtls.remove(roomId);
			return p;
		}
	}

	@Override
	public void onPeerError(String roomId, Peer peer, Throwable cause) {
		getRoom(roomId).onPeerError(peer, cause);
	}

	@Override
	public void onPeerClose(String roomId, Peer peer) {
		Room r = getRoom(roomId);
		r.onPeerLeave(peer);
		if(r.getPeers().size() == 0) {
			roomTtls.put(roomId, System.currentTimeMillis() + TTL_ROOM);
		}
	}

	@Override
	public void onPeerMessage(String roomId, Peer peer, String message) {
		getRoom(roomId).onPeerMessage(peer, message);
	}

	@Override
	public void onPeerMessage(String roomId, Peer peer, byte[] message) {
		getRoom(roomId).onPeerMessage(peer, message);
	}

	@Override
	public Room getRoom(String roomId){
		return rooms.computeIfAbsent(roomId, ri->{
			var r = newRoom(roomId);
			r.onRoomCreated();
			return r;
		});
	}

	@Override
	public Collection<Room> getRooms() {
		return rooms.values();
	}

	protected Room newRoom(String roomId){
		return new DefaultRoom(roomId, null, null, new PrintRoomEventLogger());
	}

	protected Peer newPeer(MessageSender sender) {
		return new DefaultPeer(sender);
	}

	private Map<String, Long> roomTtls = new HashMap<>();
	private static Map<String, Room> rooms = new ConcurrentHashMap<>();
	private static final int TTL_ROOM = 10 * 60 * 1000; // 空になってから10分で削除
}
