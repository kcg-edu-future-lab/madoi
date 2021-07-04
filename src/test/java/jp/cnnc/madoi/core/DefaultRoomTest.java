package jp.cnnc.madoi.core;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import jp.cnnc.madoi.core.message.EnterRoom;
import jp.cnnc.madoi.core.message.Invocation;
import jp.cnnc.madoi.core.message.MethodConfig;
import jp.cnnc.madoi.core.message.MethodConfig.SharingType;
import jp.cnnc.madoi.core.message.ObjectConfig;
import jp.cnnc.madoi.core.message.ObjectState;
import jp.cnnc.madoi.core.room.DefaultRoom;
import jp.cnnc.madoi.core.room.eventlogger.NullRoomEventLogger;
import jp.cnnc.madoi.core.room.eventlogger.OnMemoryEventLogger;

public class DefaultRoomTest {
	@Test
	public void test_peerJoin() throws Throwable{
		var room = new DefaultRoom("room1", new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", room);
		var peer2 = new MockPeer("peer2", room);
		peer1.peerArrive();
		peer2.peerArrive();
		assertEquals(0, peer1.getSentTexts().size());
		assertEquals(2, peer1.getSentMessages().size());
		assertEquals("EnterRoom", peer1.getSentMessages().get(0).getType());
		assertEquals("PeerJoin", peer1.getSentMessages().get(1).getType());
		assertEquals(0, peer2.getSentTexts().size());
		assertEquals(1, peer2.getSentMessages().size());
		assertEquals("EnterRoom", peer2.getSentMessages().get(0).getType());
		assertEquals("peer1", ((EnterRoom)peer2.getSentMessages().get(0)).getPeers().get(0).getId());
	}

	@Test
	public void test_peerLeave() throws Throwable{
		var room = new DefaultRoom("room1", new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", room);
		var peer2 = new MockPeer("peer2", room);
		var peer3 = new MockPeer("peer3", room);
		peer1.peerArrive();
		peer2.peerArrive();
		peer1.peerLeave();
		peer3.peerArrive();
		assertEquals(0, peer1.getSentTexts().size());
		assertEquals(2, peer1.getSentMessages().size());
		assertEquals("EnterRoom", peer1.getSentMessages().get(0).getType());
		assertEquals("PeerJoin", peer1.getSentMessages().get(1).getType());
		assertEquals(0, peer2.getSentTexts().size());
		assertEquals(3, peer2.getSentMessages().size());
		assertEquals("EnterRoom", peer2.getSentMessages().get(0).getType());
		assertEquals("PeerLeave", peer2.getSentMessages().get(1).getType());
		assertEquals("PeerJoin", peer2.getSentMessages().get(2).getType());
		assertEquals("peer2", ((EnterRoom)peer3.getSentMessages().get(0)).getPeers().get(0).getId());
	}

	@Test
	public void test_enterRoom() throws Throwable{
		var room = new DefaultRoom("room1", new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", room);
		var peer2 = new MockPeer("peer2", room);
		peer1.peerArrive();
		peer2.peerArrive();
		assertTrue(peer1.getSentMessages().get(0) instanceof EnterRoom);
		if(peer1.getSentMessages().get(0) instanceof EnterRoom) {
			EnterRoom er1 = (EnterRoom)peer1.getSentMessages().get(0);
			assertEquals("EnterRoom", er1.getType());
			assertEquals(0, er1.getPeers().size());
			assertTrue(peer2.getSentMessages().get(0) instanceof EnterRoom);
		} else {
			fail();
		}
		if(peer2.getSentMessages().get(0) instanceof EnterRoom) {
			EnterRoom er2 = (EnterRoom)peer2.getSentMessages().get(0);
			assertEquals("EnterRoom", er2.getType());
			assertEquals(1, er2.getPeers().size());
			assertEquals(peer1.getId(), er2.getPeers().get(0).getId());
			assertEquals(peer1.getOrder(), er2.getPeers().get(0).getOrder());
		} else {
			fail();
		}
	}

	@Test
	public void test_execAndSend() throws Throwable{
		var el = new OnMemoryEventLogger();
		var room = new DefaultRoom("room1", el);
		var peer = new MockPeer("peer1", room);

		peer.peerArrive();
		peer.peerMessage(new MethodConfig(1, 1000, SharingType.SHARE_RESULT));
		peer.peerMessage(new Invocation(1, 1, new Object[]{"arg1"}));

		assertEquals(1, peer.getSentMessages().size());
		assertEquals("EnterRoom", peer.getSentMessages().get(0).getType());
		for(Object[] m : el.getEvents()) {
			System.out.printf("%s%n", Arrays.deepToString(m));
		}

		assertEquals(5, el.getEvents().size());
		assertArrayEquals(
				new Object[]{"receiveOpen", "room1", "peer1"},
				el.getEvents().get(0));
		assertArrayEquals(
				new Object[] {"sendMessage", "room1", "SERVERNOTIFY", new String[] {"peer1"}, "EnterRoom"},
				Arrays.copyOf(el.getEvents().get(1), 5));
		assertArrayEquals(
				new Object[] {"receiveMessage", "room1", "peer1", "MethodConfig"},
				Arrays.copyOf(el.getEvents().get(2), 4));
		assertArrayEquals(
				new Object[] {"receiveMessage", "room1", "peer1", "Invocation"},
				Arrays.copyOf(el.getEvents().get(3), 4));
		assertArrayEquals(
				new Object[] {"sendMessage", "room1", "OTHERCAST", new String[]{}, "Invocation"},
				Arrays.copyOf(el.getEvents().get(4), 5));
	}

	/*
	 * ObjectStateが送られてきたら，それまでのInvocationのログがクリアされるはず。
	 */
	@Test
	public void test_eliminatLogs() throws Throwable{
		var room = new DefaultRoom("room1", new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", room);
		peer1.peerArrive();
		peer1.peerMessage(new ObjectConfig(0, Arrays.asList(0)));
		peer1.peerMessage(new MethodConfig(0, 1000, SharingType.SHARE_PROCESS));
		peer1.peerMessage(new Invocation(0, 0, new Object[] {}));
		assertEquals(1, room.getInvocationLogs().size());
		assertEquals(1, room.getInvocationLogs().get(0).size());
		peer1.peerMessage(new ObjectState(0, ""));
		assertEquals(0, room.getInvocationLogs().get(0).size());
		var peer2 = new MockPeer("peer2", room);
		peer2.peerArrive();
		if(peer2.getSentMessages().get(0) instanceof EnterRoom) {
			EnterRoom er = (EnterRoom)peer2.getSentMessages().get(0);
			assertEquals(1, er.getHistories().size());
			assertEquals("ObjectState", er.getHistories().get(0).getType());
		} else {
			fail();
		}
	}
}
