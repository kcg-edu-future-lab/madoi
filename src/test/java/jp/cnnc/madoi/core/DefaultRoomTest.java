package jp.cnnc.madoi.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import jp.cnnc.madoi.core.message.EnterRoom;
import jp.cnnc.madoi.core.message.FunctionDefinition;
import jp.cnnc.madoi.core.message.Invocation;
import jp.cnnc.madoi.core.message.MethodDefinition;
import jp.cnnc.madoi.core.message.ObjectDefinition;
import jp.cnnc.madoi.core.message.ObjectState;
import jp.cnnc.madoi.core.message.PeerJoin;
import jp.cnnc.madoi.core.message.UpdatePeerProfile;
import jp.cnnc.madoi.core.message.config.ShareConfig;
import jp.cnnc.madoi.core.message.config.ShareConfig.SharingType;
import jp.cnnc.madoi.core.room.DefaultRoom;
import jp.cnnc.madoi.core.room.eventlogger.NullRoomEventLogger;
import jp.cnnc.madoi.core.room.eventlogger.OnMemoryEventLogger;

public class DefaultRoomTest {
	@Test
	public void test_waitingPeer() throws Throwable{
		var room = new DefaultRoom("room1", new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", "Peer1", room);
		var peer2 = new MockPeer("peer2", "Peer2", room);
		peer1.peerArriveAndLoginRoom();
		peer2.peerArrive();
		peer1.sendMessage(new Invocation(1, 1, "foo", new Object[] {}));
		assertEquals(2, peer1.getSentMessageCount());
		assertEquals("EnterRoom", peer1.getSentMessageAt(0).getType());
		assertEquals("Invocation", peer1.getSentMessageAt(1).getType());
		assertEquals(0, peer2.getSentMessageCount());
		peer2.loginRoom();
		assertEquals(3, peer1.getSentMessageCount());
		assertEquals("PeerJoin", peer1.getSentMessageAt(2).getType());
		assertEquals("Peer2", ((PeerJoin)peer1.getSentMessageAt(2)).getPeer().getProfile().get("name"));
		assertEquals(1, peer2.getSentMessageCount());
		assertEquals("EnterRoom", peer2.getSentMessageAt(0).getType());
	}

	@Test
	public void test_peerJoin() throws Throwable{
		var room = new DefaultRoom("room1", new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", "Peer1", room);
		var peer2 = new MockPeer("peer2", "Peer2", room);
		peer1.peerArriveAndLoginRoom();
		peer2.peerArriveAndLoginRoom();
		assertEquals(2, peer1.getSentMessageCount());
		assertEquals("EnterRoom", peer1.getSentMessageAt(0).getType());
		assertEquals(1, ((EnterRoom)peer1.getSentMessageAt(0)).getSelfPeerOrder());
		assertEquals("PeerJoin", peer1.getSentMessageAt(1).getType());
		assertEquals(1, peer2.getSentMessageCount());
		assertEquals("EnterRoom", peer2.getSentMessageAt(0).getType());
		assertEquals(2, ((EnterRoom)peer2.getSentMessageAt(0)).getSelfPeerOrder());
		assertEquals("peer1", ((EnterRoom)peer2.getSentMessageAt(0)).getPeers().get(0).getId());
	}

	@Test
	public void test_peerLeave() throws Throwable{
		var room = new DefaultRoom("room1", new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", "Peer1", room);
		var peer2 = new MockPeer("peer2", "Peer2", room);
		var peer3 = new MockPeer("peer3", "Peer3", room);
		peer1.peerArriveAndLoginRoom();
		peer2.peerArriveAndLoginRoom();
		peer1.peerLeave();
		peer3.peerArriveAndLoginRoom();
		assertEquals(2, peer1.getSentMessages().size());
		assertEquals("EnterRoom", peer1.getSentMessages().get(0).getType());
		assertEquals("PeerJoin", peer1.getSentMessages().get(1).getType());
		assertEquals(3, peer2.getSentMessages().size());
		assertEquals("EnterRoom", peer2.getSentMessages().get(0).getType());
		assertEquals("PeerLeave", peer2.getSentMessages().get(1).getType());
		assertEquals("PeerJoin", peer2.getSentMessages().get(2).getType());
		assertEquals("peer2", ((EnterRoom)peer3.getSentMessages().get(0)).getPeers().get(0).getId());
	}

	@Test
	public void test_updatePeerProfile() throws Throwable{
		var room = new DefaultRoom("room1", new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", "Peer1", room);
		var peer2 = new MockPeer("peer2", "Peer2", room);
		var peer3 = new MockPeer("peer3", "Peer3", room);
		peer1.peerArriveAndLoginRoom();
		peer2.peerArriveAndLoginRoom();
		peer1.updatePeerProfileChangeName("Peer1NewName");
		peer3.peerArriveAndLoginRoom();
		assertEquals(3, peer1.getSentMessages().size());
		assertEquals("EnterRoom", peer1.getSentMessages().get(0).getType());
		assertEquals("PeerJoin", peer1.getSentMessages().get(1).getType());
		assertEquals("PeerJoin", peer1.getSentMessages().get(2).getType());
		assertEquals(3, peer2.getSentMessages().size());
		assertEquals("EnterRoom", peer2.getSentMessages().get(0).getType());
		assertEquals("UpdatePeerProfile", peer2.getSentMessages().get(1).getType());
		assertEquals("Peer1NewName", ((UpdatePeerProfile)peer2.getSentMessages().get(1)).getUpdates().get("name"));
		assertEquals("PeerJoin", peer2.getSentMessages().get(2).getType());
		assertEquals("peer1", ((EnterRoom)peer3.getSentMessages().get(0)).getPeers().get(0).getId());
		assertEquals("Peer1NewName", ((EnterRoom)peer3.getSentMessages().get(0)).getPeers().get(0).getProfile().get("name"));
	}

	@Test
	public void test_enterRoom() throws Throwable{
		var room = new DefaultRoom("room1", new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", "Peer1", room);
		var peer2 = new MockPeer("peer2", "Peer2", room);
		peer1.peerArriveAndLoginRoom();
		peer2.peerArriveAndLoginRoom();
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
		var peer = new MockPeer("peer1", "Peer1", room);

		peer.peerArriveAndLoginRoom();
		peer.peerMessage(new FunctionDefinition(1, "foo", new ShareConfig(SharingType.afterExec, 0)));
		peer.peerMessage(new Invocation(1, 1, "foo", new Object[]{"arg1"}));

		assertEquals(1, peer.getSentMessages().size());
		assertEquals("EnterRoom", ((Message)peer.getSentMessages().get(0)).getType());
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
				new Object[] {"receiveMessage", "room1", "peer1", "FunctionDefinition"},
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
	public void test_eliminateLogs() throws Throwable{
		var room = new DefaultRoom("room1", new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", "Peer1", room);
		peer1.peerArriveAndLoginRoom();
		peer1.peerMessage(new ObjectDefinition(0, "Test", Arrays.asList(
				new MethodDefinition(0, "foo", new ShareConfig(SharingType.beforeExec, 1000))
				)));
		peer1.peerMessage(new Invocation(0, "foo", new Object[] {}));
		assertEquals(1, room.getInvocationLogs().size());
		assertEquals(1, room.getInvocationLogs().get(0).size());
		peer1.peerMessage(new ObjectState(0, "", 0));
		assertEquals(0, room.getInvocationLogs().get(0).size());
		var peer2 = new MockPeer("peer2", "Peer2", room);
		peer2.peerArriveAndLoginRoom();
		if(peer2.getSentMessages().get(0) instanceof EnterRoom) {
			EnterRoom er = (EnterRoom)peer2.getSentMessages().get(0);
			assertEquals(1, er.getHistories().size());
			assertEquals("ObjectState", er.getHistories().get(0).getType());
		} else {
			fail();
		}
	}

	/*
	 * ObjectStateが送られてきたら，それまでのInvocationのログがクリアされるはず。
	 */
	@Test
	public void test_eliminateLogs2() throws Throwable{
		var room = new DefaultRoom("room1", new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", "Peer1", room);
		var peer2 = new MockPeer("peer2", "Peer2", room);

		peer1.peerArriveAndLoginRoom();
		peer1.peerMessage(new ObjectDefinition(0, "Test", Arrays.asList(
				new MethodDefinition(0, "foo", new ShareConfig(SharingType.beforeExec, 1000)),
				new MethodDefinition(1, "bar", new ShareConfig(SharingType.beforeExec, 1000))
				)));
		peer1.peerMessage(new ObjectDefinition(1, "Test2", Arrays.asList(
				new MethodDefinition(2, "foo2", new ShareConfig(SharingType.beforeExec, 1000))
				)));
		peer1.peerMessage(new Invocation(0, 0, "foo", new Object[] {}));
		peer1.peerMessage(new Invocation(0, 1, "bar", new Object[] {}));
		peer1.peerMessage(new Invocation(1, 2, "foo2", new Object[] {}));
		assertEquals(3, room.getInvocationLogs().size());
		assertEquals(1, room.getInvocationLogs().get(0).size());
		assertEquals(1, room.getInvocationLogs().get(1).size());
		assertEquals(1, room.getInvocationLogs().get(2).size());

		peer1.peerMessage(new ObjectState(0, "", 0));
		assertEquals(0, room.getInvocationLogs().get(0).size());
		assertEquals(0, room.getInvocationLogs().get(1).size());
		assertEquals(1, room.getInvocationLogs().get(2).size());

		peer2.peerArriveAndLoginRoom();
		if(peer2.getSentMessages().get(0) instanceof EnterRoom) {
			EnterRoom er = (EnterRoom)peer2.getSentMessages().get(0);
			assertEquals(2, er.getHistories().size());
			assertEquals("ObjectState", er.getHistories().get(0).getType());
			assertEquals("Invocation", er.getHistories().get(1).getType());
		} else {
			fail();
		}
	}

	@Test
	public void test_peerLeaveOnIOE() throws Throwable{
		var el = new OnMemoryEventLogger();
		var room = new DefaultRoom("room1", el);
		var peer1 = new MockPeer("peer1", "Peer1", room);
		var peer2 = new MockPeer("peer2", "Peer2", room);

		peer1.peerArriveAndLoginRoom();
		peer2.peerArriveAndLoginRoom();
		peer1.peerMessage(new ObjectDefinition(0, "Test", Arrays.asList(
				new MethodDefinition(0, "foo", new ShareConfig(SharingType.beforeExec, 1000))
				)));
		peer1.peerMessage(new Invocation(0, "foo", new Object[] {}));
		peer2.setIoeOnSend(true);
		peer1.peerMessage(new Invocation(0, "foo", new Object[] {}));
		{
			var msgs = peer1.getSentMessages();
			assertEquals(5, msgs.size());
			assertEquals("EnterRoom", msgs.get(0).getType());
			assertEquals("PeerJoin", msgs.get(1).getType());
			assertEquals("Invocation", msgs.get(2).getType());
			assertEquals("Invocation", msgs.get(3).getType());
			assertEquals("PeerLeave", msgs.get(4).getType());
		}
		{
			var msgs = peer2.getSentMessages();
			assertEquals(2, msgs.size());
			assertEquals("EnterRoom", msgs.get(0).getType());
			assertEquals("Invocation", msgs.get(1).getType());
		}
		{
			var events = el.getEvents();
			assertEquals(10, events.size());
			assertEquals("receiveOpen", events.get(0)[0]);
			assertEquals("sendMessage", events.get(1)[0]);
			assertEquals("receiveOpen", events.get(2)[0]);
			assertEquals("sendMessage", events.get(3)[0]);
			assertEquals("receiveMessage", events.get(4)[0]);
			assertEquals("receiveMessage", events.get(5)[0]);
			assertEquals("sendMessage", events.get(6)[0]);
			assertEquals("receiveMessage", events.get(7)[0]);
			assertEquals("sendMessage", events.get(8)[0]);
			assertEquals("Invocation", events.get(8)[4]);
			assertEquals("sendMessage", events.get(9)[0]);
			assertEquals("PeerLeave", events.get(9)[4]);
		}
	}
}
