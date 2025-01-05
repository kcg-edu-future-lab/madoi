package edu.kcg.futurelab.madoi.core.room;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import edu.kcg.futurelab.madoi.core.room.logger.PrintRoomEventLogger;

public class DefaultRoomManager implements RoomManager{
	public DefaultRoomManager() {
/*
		// dummy room
		var dr = new DefaultRoom("dummyRoom", new RoomSpec(100),
				Collections.emptyMap(), new NullRoomEventLogger());
		dr.getFunctionRuntimeInfos().put(0, new FunctionRuntimeInfo(
				new FunctionDefinition(0, "func", new FunctionConfig()),
				1, new Date()));
		var md = new MethodDefinition(0, "method", new MethodConfig());
		dr.getObjectRuntimeInfos().put(0, new ObjectRuntimeInfo(
				new ObjectDefinition(0, "obj", new ShareClassConfig("Object"),
						new ArrayList<MethodDefinition>() {{
							add(md);
						}}
						),
				null, 0,
				new LinkedHashMap<Integer, MethodRuntimeInfo>(){{
					put(0, new MethodRuntimeInfo(md, 1, new Date()));
				}}
				));
		rooms.put(dr.getId(), dr);
//*/
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
			synchronized(roomTtls) {
				var it = roomTtls.entrySet().iterator();
				var cur = System.currentTimeMillis();
				while(it.hasNext()) {
					var e = it.next();
					if(cur > e.getValue()) {
						var room = rooms.remove(e.getKey());
						it.remove();
						room.getPeers().forEach(p->{
							try {
								p.getConnection().close();
							} catch(IOException ex) {
							}
						});
						room.onRoomDestroyed();
					}
				}
			}
		}, 10, 10, TimeUnit.SECONDS);
	}

	@Override
	public Peer onPeerOpen(String roomId, Connection sender) {
		synchronized(roomTtls) {
			var r = getOrCreateRoom(roomId);
			var p = newPeer(sender);
			r.onPeerArrive(p);
			roomTtls.remove(roomId);
			return p;
		}
	}

	@Override
	public void onPeerError(String roomId, Peer peer, Throwable cause) {
		getOrCreateRoom(roomId).onPeerError(peer, cause);
	}

	@Override
	public void onPeerClose(String roomId, Peer peer) {
		Room r = getOrCreateRoom(roomId);
		r.onPeerLeave(peer);
		if(r.getPeers().size() == 0) {
			roomTtls.put(roomId, System.currentTimeMillis() + TTL_ROOM);
		}
	}

	@Override
	public void onPeerMessage(String roomId, Peer peer, String message) {
		getOrCreateRoom(roomId).onPeerMessage(peer, message);
	}

	@Override
	public void onPeerMessage(String roomId, Peer peer, byte[] message) {
		getOrCreateRoom(roomId).onPeerMessage(peer, message);
	}

	@Override
	public Room getOrCreateRoom(String roomId){
		return rooms.computeIfAbsent(roomId, ri->{
			var r = newRoom(roomId);
			r.onRoomCreated();
			return r;
		});
	}

	@Override
	public Room getRoom(String roomId){
		return rooms.get(roomId);
	}

	@Override
	public Collection<Room> getRooms() {
		return rooms.values();
	}

	protected Room newRoom(String roomId){
		return new DefaultRoom(roomId, null, null, new PrintRoomEventLogger());
	}

	protected Peer newPeer(Connection sender) {
		return new DefaultPeer(sender);
	}

	private Map<String, Long> roomTtls = new HashMap<>();
	private static Map<String, Room> rooms = new ConcurrentHashMap<>();
	private static final int TTL_ROOM = 10 * 60 * 1000; // 空になってから10分で削除
}
