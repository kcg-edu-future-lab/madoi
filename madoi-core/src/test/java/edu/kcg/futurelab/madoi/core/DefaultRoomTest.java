package edu.kcg.futurelab.madoi.core;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import edu.kcg.futurelab.madoi.core.message.DefineFunction;
import edu.kcg.futurelab.madoi.core.message.DefineObject;
import edu.kcg.futurelab.madoi.core.message.EnterRoomAllowed;
import edu.kcg.futurelab.madoi.core.message.InvokeMethod;
import edu.kcg.futurelab.madoi.core.message.Message;
import edu.kcg.futurelab.madoi.core.message.PeerEntered;
import edu.kcg.futurelab.madoi.core.message.PeerLeaved;
import edu.kcg.futurelab.madoi.core.message.UpdateObjectState;
import edu.kcg.futurelab.madoi.core.message.UpdatePeerProfile;
import edu.kcg.futurelab.madoi.core.message.config.FunctionConfig;
import edu.kcg.futurelab.madoi.core.message.config.ShareConfig;
import edu.kcg.futurelab.madoi.core.message.config.ShareConfig.SharingType;
import edu.kcg.futurelab.madoi.core.message.definition.FunctionDefinition;
import edu.kcg.futurelab.madoi.core.message.definition.MethodDefinition;
import edu.kcg.futurelab.madoi.core.message.definition.ObjectDefinition;
import edu.kcg.futurelab.madoi.core.room.DefaultRoom;
import edu.kcg.futurelab.madoi.core.room.logger.NullRoomEventLogger;
import edu.kcg.futurelab.madoi.core.room.logger.OnMemoryEventLogger;

public class DefaultRoomTest {
	@Test
	public void test_waitingPeer() throws Throwable{
		var room = new DefaultRoom("room1", null, new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", "Peer1", room);
		var peer2 = new MockPeer("peer2", "Peer2", room);
		peer1.peerArriveAndLoginRoom();
		peer2.peerArrive();
		peer1.getSender().send(new InvokeMethod(1, 1, 1, new Object[] {}));
		assertEquals(2, peer1.getSentMessageCount());
		assertEquals(EnterRoomAllowed.class.getSimpleName(), peer1.getSentMessageAt(0).getType());
		assertEquals(InvokeMethod.class.getSimpleName(), peer1.getSentMessageAt(1).getType());
		assertEquals(0, peer2.getSentMessageCount());
		peer2.loginRoom();
		assertEquals(3, peer1.getSentMessageCount());
		assertEquals(PeerEntered.class.getSimpleName(), peer1.getSentMessageAt(2).getType());
		assertEquals("Peer2", ((PeerEntered)peer1.getSentMessageAt(2)).getPeer().getProfile().get("name"));
		assertEquals(1, peer2.getSentMessageCount());
		assertEquals(EnterRoomAllowed.class.getSimpleName(), peer2.getSentMessageAt(0).getType());
	}

	@Test
	public void test_peerEntered() throws Throwable{
		var room = new DefaultRoom("room1", null, new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", "Peer1", room);
		var peer2 = new MockPeer("peer2", "Peer2", room);
		peer1.peerArriveAndLoginRoom();
		peer2.peerArriveAndLoginRoom();
		assertEquals(2, peer1.getSentMessageCount());
		assertEquals("EnterRoomAllowed", peer1.getSentMessageAt(0).getType());
		assertEquals(1, ((EnterRoomAllowed)peer1.getSentMessageAt(0)).getSelfPeer().getOrder());
		assertEquals("PeerEntered", peer1.getSentMessageAt(1).getType());
		assertEquals(1, peer2.getSentMessageCount());
		assertEquals("EnterRoomAllowed", peer2.getSentMessageAt(0).getType());
		assertEquals(2, ((EnterRoomAllowed)peer2.getSentMessageAt(0)).getSelfPeer().getOrder());
		assertEquals("peer1", ((EnterRoomAllowed)peer2.getSentMessageAt(0)).getOtherPeers().get(0).getId());
	}

	@Test
	public void test_peerLeaved() throws Throwable{
		var room = new DefaultRoom("room1", null, new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", "Peer1", room);
		var peer2 = new MockPeer("peer2", "Peer2", room);
		var peer3 = new MockPeer("peer3", "Peer3", room);
		peer1.peerArriveAndLoginRoom();
		peer2.peerArriveAndLoginRoom();
		peer1.peerLeave();
		peer3.peerArriveAndLoginRoom();
		assertEquals(2, peer1.getSentMessages().size());
		assertEquals("EnterRoomAllowed", peer1.getSentMessages().get(0).getType());
		assertEquals("PeerEntered", peer1.getSentMessages().get(1).getType());
		assertEquals(3, peer2.getSentMessages().size());
		assertEquals("EnterRoomAllowed", peer2.getSentMessages().get(0).getType());
		assertEquals("PeerLeaved", peer2.getSentMessages().get(1).getType());
		assertEquals("PeerEntered", peer2.getSentMessages().get(2).getType());
		assertEquals("peer2", ((EnterRoomAllowed)peer3.getSentMessages().get(0)).getOtherPeers().get(0).getId());
	}

	@Test
	public void test_updatePeerProfile() throws Throwable{
		var room = new DefaultRoom("room1", null, new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", "Peer1", room);
		var peer2 = new MockPeer("peer2", "Peer2", room);
		var peer3 = new MockPeer("peer3", "Peer3", room);
		peer1.peerArriveAndLoginRoom();
		peer2.peerArriveAndLoginRoom();
		peer1.updatePeerProfileChangeName("Peer1NewName");
		peer3.peerArriveAndLoginRoom();
		assertEquals(3, peer1.getSentMessages().size());
		assertEquals(EnterRoomAllowed.class.getSimpleName(), peer1.getSentMessages().get(0).getType());
		assertEquals(PeerEntered.class.getSimpleName(), peer1.getSentMessages().get(1).getType());
		assertEquals(PeerEntered.class.getSimpleName(), peer1.getSentMessages().get(2).getType());
		assertEquals(3, peer2.getSentMessages().size());
		assertEquals(EnterRoomAllowed.class.getSimpleName(), peer2.getSentMessages().get(0).getType());
		assertEquals(UpdatePeerProfile.class.getSimpleName(), peer2.getSentMessages().get(1).getType());
		assertEquals("Peer1NewName", ((UpdatePeerProfile)peer2.getSentMessages().get(1)).getUpdates().get("name"));
		assertEquals(PeerEntered.class.getSimpleName(), peer2.getSentMessages().get(2).getType());
		assertEquals("peer1", ((EnterRoomAllowed)peer3.getSentMessages().get(0)).getOtherPeers().get(0).getId());
		assertEquals("Peer1NewName", ((EnterRoomAllowed)peer3.getSentMessages().get(0)).getOtherPeers().get(0).getProfile().get("name"));
	}

	@Test
	public void test_enterRoom() throws Throwable{
		var room = new DefaultRoom("room1", null, new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", "Peer1", room);
		var peer2 = new MockPeer("peer2", "Peer2", room);
		peer1.peerArriveAndLoginRoom();
		peer2.peerArriveAndLoginRoom();
		assertTrue(peer1.getSentMessages().get(0) instanceof EnterRoomAllowed);
		if(peer1.getSentMessages().get(0) instanceof EnterRoomAllowed) {
			EnterRoomAllowed er1 = (EnterRoomAllowed)peer1.getSentMessages().get(0);
			assertEquals("EnterRoomAllowed", er1.getType());
			assertEquals(0, er1.getOtherPeers().size());
			assertTrue(peer2.getSentMessages().get(0) instanceof EnterRoomAllowed);
		} else {
			fail();
		}
		if(peer2.getSentMessages().get(0) instanceof EnterRoomAllowed) {
			EnterRoomAllowed er2 = (EnterRoomAllowed)peer2.getSentMessages().get(0);
			assertEquals("EnterRoomAllowed", er2.getType());
			assertEquals(1, er2.getOtherPeers().size());
			assertEquals(peer1.getId(), er2.getOtherPeers().get(0).getId());
			assertEquals(peer1.getOrder(), er2.getOtherPeers().get(0).getOrder());
		} else {
			fail();
		}
	}

	@Test
	public void test_execAndSend() throws Throwable{
		var el = new OnMemoryEventLogger();
		var room = new DefaultRoom("room1", null, el);
		var peer = new MockPeer("peer1", "Peer1", room);

		peer.peerArriveAndLoginRoom();
		peer.peerMessage(new DefineFunction(new FunctionDefinition(
				1, "foo", new FunctionConfig(new ShareConfig(SharingType.afterExec, 0)))));
		peer.peerMessage(new InvokeMethod(1, 1, 1, new Object[]{"arg1"}));

		assertEquals(1, peer.getSentMessages().size());
		assertEquals("EnterRoomAllowed", peer.getSentMessages().get(0).getType());
		for(Object[] m : el.getEvents()) {
			System.out.printf("%s%n", Arrays.deepToString(m));
		}

		assertEquals(5, el.getEvents().size());
		assertArrayEquals(
				new Object[]{"receiveOpen", "room1", "peer1"},
				el.getEvents().get(0));
		assertArrayEquals(
				new Object[] {"sendMessage", "room1", "SERVERNOTIFY", new String[] {"peer1"}, "EnterRoomAllowed"},
				Arrays.copyOf(el.getEvents().get(1), 5));
		assertArrayEquals(
				new Object[] {"receiveMessage", "room1", "peer1", "DefineFunction"},
				Arrays.copyOf(el.getEvents().get(2), 4));
		assertArrayEquals(
				new Object[] {"receiveMessage", "room1", "peer1", "InvokeMethod"},
				Arrays.copyOf(el.getEvents().get(3), 4));
		assertArrayEquals(
				new Object[] {"sendMessage", "room1", "OTHERCAST", new String[]{}, "InvokeMethod"},
				Arrays.copyOf(el.getEvents().get(4), 5));
	}

	/*
	 * ObjectStateが送られてきたら，それまでのInvocationのログがクリアされるはず。
	 */
	@Test
	public void test_eliminateLogs() throws Throwable{
		var room = new DefaultRoom("room1", null, new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", "Peer1", room);
		peer1.peerArriveAndLoginRoom();
		peer1.peerMessage(new DefineObject(new ObjectDefinition(0, "Test", null, Arrays.asList(
				new MethodDefinition(0, "foo", new ShareConfig(SharingType.beforeExec, 1000))
				))));
		peer1.peerMessage(new InvokeMethod(0, 0, 0, new Object[] {}));
		assertEquals(1, room.getMessageHistories().size());
		assertEquals(1, room.getObjectRuntimeInfos().get(0).getRevision());
		assertEquals(1, room.getObjectRuntimeInfos().get(0).getMethods().get(0).getInvocationCount());
		peer1.peerMessage(new UpdateObjectState(0, "", 0));
		assertEquals(0, room.getMessageHistories().size());
		var peer2 = new MockPeer("peer2", "Peer2", room);
		peer2.peerArriveAndLoginRoom();
		if(peer2.getSentMessages().get(0) instanceof EnterRoomAllowed) {
			EnterRoomAllowed er = (EnterRoomAllowed)peer2.getSentMessages().get(0);
			assertEquals(1, er.getHistories().size());
			assertEquals("NotifyObjectState", ((Message)er.getHistories().get(0)).getType());
		} else {
			fail();
		}
	}

	/*
	 * ObjectStateが送られてきたら，それまでのInvocationのログがクリアされるはず。
	 */
	@Test
	public void test_eliminateLogs2() throws Throwable{
		var room = new DefaultRoom("room1", null, new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", "Peer1", room);
		var peer2 = new MockPeer("peer2", "Peer2", room);

		peer1.peerArriveAndLoginRoom();
		peer1.peerMessage(new DefineObject(new ObjectDefinition(0, "Test", null, Arrays.asList(
				new MethodDefinition(0, "foo", new ShareConfig(SharingType.beforeExec, 1000)),
				new MethodDefinition(1, "bar", new ShareConfig(SharingType.beforeExec, 1000))
				))));
		peer1.peerMessage(new DefineObject(new ObjectDefinition(1, "Test2", null, Arrays.asList(
				new MethodDefinition(2, "foo2", new ShareConfig(SharingType.beforeExec, 1000))
				))));
		peer1.peerMessage(new InvokeMethod(0, 0, 0, new Object[] {}));
		peer1.peerMessage(new InvokeMethod(0, 1, 1, new Object[] {}));
		peer1.peerMessage(new InvokeMethod(1, 2, 2, new Object[] {}));
		assertEquals(3, room.getMessageHistories().size());
		// TODO: 要修正。特定のメソッドの呼び出し履歴が記録されているかは現状わからない
		assertEquals(1, room.getObjectRuntimeInfos().get(0).getMethods().get(0).getInvocationCount());
		assertEquals(1, room.getObjectRuntimeInfos().get(0).getMethods().get(1).getInvocationCount());
		assertEquals(1, room.getObjectRuntimeInfos().get(0).getMethods().get(2).getInvocationCount());

		peer1.peerMessage(new UpdateObjectState(0, "", 0));
		// TODO: 要修正。特定のメソッドの呼び出し履歴が記録されているかは現状わからない
		assertEquals(0, room.getObjectRuntimeInfos().get(0).getMethods().get(0).getInvocationCount());
		assertEquals(0, room.getObjectRuntimeInfos().get(0).getMethods().get(1).getInvocationCount());
		assertEquals(1, room.getObjectRuntimeInfos().get(0).getMethods().get(2).getInvocationCount());

		peer2.peerArriveAndLoginRoom();
		if(peer2.getSentMessages().get(0) instanceof EnterRoomAllowed) {
			EnterRoomAllowed er = (EnterRoomAllowed)peer2.getSentMessages().get(0);
			assertEquals(2, er.getHistories().size());
			assertEquals("UpdateObjectState", ((UpdateObjectState)er.getHistories().get(0)).getType());
			assertEquals("InvokeMethod", ((InvokeMethod)er.getHistories().get(1)).getType());
		} else {
			fail();
		}
	}

	@Test
	public void test_peerLeaveOnIOE() throws Throwable{
		var el = new OnMemoryEventLogger();
		var room = new DefaultRoom("room1", null, el);
		var peer1 = new MockPeer("peer1", "Peer1", room);
		var peer2 = new MockPeer("peer2", "Peer2", room);

		peer1.peerArriveAndLoginRoom();
		peer2.peerArriveAndLoginRoom();
		peer1.peerMessage(new DefineObject(new ObjectDefinition(0, "Test", null, Arrays.asList(
				new MethodDefinition(0, "foo", new ShareConfig(SharingType.beforeExec, 1000))
				))));
		peer1.peerMessage(new InvokeMethod(0, 0, 0, new Object[] {}));
		peer2.setIoeOnSend(true);
		peer1.peerMessage(new InvokeMethod(0, 1, 0, new Object[] {}));
		{
			var msgs = peer1.getSentMessages();
			assertEquals(5, msgs.size());
			assertEquals(EnterRoomAllowed.class.getSimpleName(), msgs.get(0).getType());
			assertEquals(PeerEntered.class.getSimpleName(), msgs.get(1).getType());
			assertEquals(InvokeMethod.class.getSimpleName(), msgs.get(2).getType());
			assertEquals(InvokeMethod.class.getSimpleName(), msgs.get(3).getType());
			assertEquals(PeerLeaved.class.getSimpleName(), msgs.get(4).getType());
		}
		{
			var msgs = peer2.getSentMessages();
			assertEquals(2, msgs.size());
			assertEquals(EnterRoomAllowed.class.getSimpleName(), msgs.get(0).getType());
			assertEquals(InvokeMethod.class.getSimpleName(), msgs.get(1).getType());
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
			assertEquals(InvokeMethod.class.getSimpleName(), events.get(8)[4]);
			assertEquals("sendMessage", events.get(9)[0]);
			assertEquals("PeerLeave", events.get(9)[4]);
		}
	}
}
