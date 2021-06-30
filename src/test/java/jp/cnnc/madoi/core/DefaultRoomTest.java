package jp.cnnc.madoi.core;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.cnnc.madoi.core.message.Invocation;
import jp.cnnc.madoi.core.message.RoomEnter;
import jp.cnnc.madoi.core.room.DefaultRoom;
import jp.cnnc.madoi.core.room.eventlogger.NullRoomEventLogger;
import jp.cnnc.madoi.core.util.JsonUtil;

public class DefaultRoomTest {
	@Test
	public void test_peerJoin() throws Throwable{
		var room = new DefaultRoom("room1", new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1");
		var peer2 = new MockPeer("peer2");
		room.onPeerArrive(peer1);
		room.onPeerArrive(peer2);
		assertEquals(0, peer1.getSentTexts().size());
		assertEquals(2, peer1.getSentMessages().size());
		assertEquals("RoomEnter", peer1.getSentMessages().get(0).getType());
		assertEquals("PeerJoin", peer1.getSentMessages().get(1).getType());
		assertEquals(0, peer2.getSentTexts().size());
		assertEquals(1, peer2.getSentMessages().size());
		assertEquals("RoomEnter", peer2.getSentMessages().get(0).getType());
		assertEquals("peer1", ((RoomEnter)peer2.getSentMessages().get(0)).getPeers().get(0));
	}

	@Test
	public void test_peerLeave() throws Throwable{
		var room = new DefaultRoom("room1", new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1");
		var peer2 = new MockPeer("peer2");
		var peer3 = new MockPeer("peer3");
		room.onPeerArrive(peer1);
		room.onPeerArrive(peer2);
		room.onPeerLeave(peer1.getId());
		room.onPeerArrive(peer3);
		assertEquals(0, peer1.getSentTexts().size());
		assertEquals(2, peer1.getSentMessages().size());
		assertEquals("RoomEnter", peer1.getSentMessages().get(0).getType());
		assertEquals("PeerJoin", peer1.getSentMessages().get(1).getType());
		assertEquals(0, peer2.getSentTexts().size());
		assertEquals(3, peer2.getSentMessages().size());
		assertEquals("RoomEnter", peer2.getSentMessages().get(0).getType());
		assertEquals("PeerLeave", peer2.getSentMessages().get(1).getType());
		assertEquals("PeerJoin", peer2.getSentMessages().get(2).getType());
		assertEquals("peer2", ((RoomEnter)peer3.getSentMessages().get(0)).getPeers().get(0));
	}

	@Test
	public void test_execAndSend() throws Throwable{
		var room = new DefaultRoom("room1", new NullRoomEventLogger());
		var session = new MockPeer("dummy1");
		room.onPeerArrive(session);
		var mapper = new ObjectMapper();

		var methodConfig = mapper.createObjectNode();
		methodConfig.put("type", "methodConfig");
		var body = mapper.createObjectNode();
		methodConfig.set("body", body);
		body.put("index", 1);
		var option = mapper.createObjectNode();
		body.set("option", option);
		option.put("type", "execAndSend");
		option.put("keep", "log");
		option.put("maxLog", 1000);
		room.onPeerMessage(session.getId(), methodConfig.toString());

		var invocation = mapper.readTree(
				mapper.writeValueAsString(new Invocation(1, 1, new String[]{"arg1"})));
		room.onPeerMessage(session.getId(), invocation.toString());

		for(Message m : session.getSentMessages()){
			System.out.println(JsonUtil.toString(m));
		}
	}
}
