package edu.kcg.futurelab.madoi.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.kcg.futurelab.madoi.core.message.DefineFunction;
import edu.kcg.futurelab.madoi.core.message.DefineObject;
import edu.kcg.futurelab.madoi.core.message.EnterRoomAllowed;
import edu.kcg.futurelab.madoi.core.message.InvokeFunction;
import edu.kcg.futurelab.madoi.core.message.InvokeMethod;
import edu.kcg.futurelab.madoi.core.message.PeerEntered;
import edu.kcg.futurelab.madoi.core.message.PeerLeaved;
import edu.kcg.futurelab.madoi.core.message.UpdateObjectState;
import edu.kcg.futurelab.madoi.core.message.UpdatePeerProfile;
import edu.kcg.futurelab.madoi.core.message.config.FunctionConfig;
import edu.kcg.futurelab.madoi.core.message.config.MethodConfig;
import edu.kcg.futurelab.madoi.core.message.config.NotifyConfig;
import edu.kcg.futurelab.madoi.core.message.config.NotifyConfig.NotifyType;
import edu.kcg.futurelab.madoi.core.message.config.ShareClassConfig;
import edu.kcg.futurelab.madoi.core.message.config.ShareConfig;
import edu.kcg.futurelab.madoi.core.message.config.ShareConfig.SharingType;
import edu.kcg.futurelab.madoi.core.message.definition.FunctionDefinition;
import edu.kcg.futurelab.madoi.core.message.definition.MethodDefinition;
import edu.kcg.futurelab.madoi.core.message.definition.ObjectDefinition;
import edu.kcg.futurelab.madoi.core.message.info.RoomSpec;
import edu.kcg.futurelab.madoi.core.room.DefaultRoom;
import edu.kcg.futurelab.madoi.core.room.logger.NullRoomEventLogger;
import edu.kcg.futurelab.madoi.core.room.logger.OnMemoryEventLogger;

public class DefaultRoomTest {
	@Test
	public void test_peerArriveThenLogin() throws Throwable{
		var room = new DefaultRoom("room1", new RoomSpec(),null, new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", room);
		peer1.peerArriveAndLoginRoom();
		assertEquals(1, peer1.getSentMessageCount());
		assertEquals("EnterRoomAllowed", peer1.getSentMessageAt(0).getType());
		assertEquals(1, ((EnterRoomAllowed)peer1.getSentMessageAt(0)).getSelfPeer().getOrder());
	}

	@Test
	public void test_2peersEntered() throws Throwable{
		var room = new DefaultRoom("room1", new RoomSpec(),null, new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", room);
		var peer2 = new MockPeer("peer2", room);
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
	public void test_waitingPeer() throws Throwable{
		var room = new DefaultRoom("room1", new RoomSpec(), null, new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", room);
		var peer2 = new MockPeer("peer2", room);
		peer1.peerArriveAndLoginRoom();
		peer2.peerArrive();
		peer1.getConnection().send(new InvokeMethod(1, 1, 1, new Object[] {}));
		assertEquals(2, peer1.getSentMessageCount());
		assertEquals(EnterRoomAllowed.class.getSimpleName(), peer1.getSentMessageAt(0).getType());
		assertEquals(InvokeMethod.class.getSimpleName(), peer1.getSentMessageAt(1).getType());
		assertEquals(0, peer2.getSentMessageCount());
		peer2.loginRoom();
		assertEquals(3, peer1.getSentMessageCount());
		assertEquals(PeerEntered.class.getSimpleName(), peer1.getSentMessageAt(2).getType());
		assertEquals(1, peer2.getSentMessageCount());
		assertEquals(EnterRoomAllowed.class.getSimpleName(), peer2.getSentMessageAt(0).getType());
	}

	@Test
	public void test_peerLeaved() throws Throwable{
		var room = new DefaultRoom("room1", null, null, new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", room);
		var peer2 = new MockPeer("peer2", room);
		var peer3 = new MockPeer("peer3", room);
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
		var room = new DefaultRoom("room1", new RoomSpec(), null, new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", room);
		var peer2 = new MockPeer("peer2", room);
		var peer3 = new MockPeer("peer3", room);
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
		var room = new DefaultRoom("room1", null, null, new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", room);
		var peer2 = new MockPeer("peer2", room);
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
		var room = new DefaultRoom("room1", null, null, el);
		var peer = new MockPeer("peer1", room);

		peer.peerArriveAndLoginRoom();
		peer.peerMessage(new DefineFunction(new FunctionDefinition(
				1, "foo", new FunctionConfig(new ShareConfig(SharingType.afterExec, 0)))));
		peer.peerMessage(new InvokeFunction(1, new Object[]{"arg1"}));

		assertEquals(1, peer.getSentMessages().size());
		assertEquals("EnterRoomAllowed", peer.getSentMessages().get(0).getType());
		assertEquals(5, el.getEvents().size());
		assertArrayEquals(
				new Object[]{"peerOpen", "room1", "peer1"},
				el.getEvents().get(0));
		assertArrayEquals(
				new Object[] {"messageCast", "room1", "SERVERTOPEER", new String[] {"peer1"}, "EnterRoomAllowed"},
				Arrays.copyOf(el.getEvents().get(1), 5));
		assertArrayEquals(
				new Object[] {"peerMessage", "room1", "peer1", "DefineFunction"},
				Arrays.copyOf(el.getEvents().get(2), 4));
		assertArrayEquals(
				new Object[] {"peerMessage", "room1", "peer1", "InvokeFunction"},
				Arrays.copyOf(el.getEvents().get(3), 4));
		assertArrayEquals(
				new Object[] {"messageCast", "room1", "OTHERCAST", new String[]{}, "InvokeFunction"},
				Arrays.copyOf(el.getEvents().get(4), 5));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void test_invocationOfShareAndNotify() throws Throwable{
		var el = new OnMemoryEventLogger();
		var room = new DefaultRoom("room1", null, null, el);
		var peer = new MockPeer("peer1", room);

		// 1つめのpeerがログインしてshareFunc,notifyFunc,shareFuncを実行
		peer.peerArriveAndLoginRoom();
		peer.peerMessage(new DefineObject(new ObjectDefinition(
				1, "TestClass", new ShareClassConfig("TestClass"),
				List.of(
					new MethodDefinition(1, "shareFunc", new MethodConfig(new ShareConfig(SharingType.beforeExec, -1))),
					new MethodDefinition(2, "notifyFunc", new MethodConfig(new NotifyConfig(NotifyType.beforeExec)))
				)
			)));
		peer.peerMessage(new InvokeMethod(1, 0, 1));
		peer.peerMessage(new InvokeMethod(1, 0, 2));
		peer.peerMessage(new InvokeMethod(1, 1, 1));

		// 1つめのpeerが受け取るメッセージは4つ
		assertEquals(4, peer.getSentMessages().size());
		assertEquals(EnterRoomAllowed.class.getSimpleName(), peer.getSentMessages().get(0).getType());
		assertEquals(InvokeMethod.class.getSimpleName(), peer.getSentMessages().get(1).getType());
		assertEquals(1, ((InvokeMethod)peer.getSentMessages().get(1)).getMethodId());
		assertEquals(InvokeMethod.class.getSimpleName(), peer.getSentMessages().get(2).getType());
		assertEquals(2, ((InvokeMethod)peer.getSentMessages().get(2)).getMethodId());
		assertEquals(InvokeMethod.class.getSimpleName(), peer.getSentMessages().get(3).getType());
		assertEquals(1, ((InvokeMethod)peer.getSentMessages().get(3)).getMethodId());

		// roomに蓄積されるオブジェクト履歴は2つ
		var histories = room.getMessageHistories();
		assertEquals(2, histories.size());
		assertEquals(InvokeMethod.class.getSimpleName(), histories.get(0).getMessageType());
		assertEquals(1, ((InvokeMethod)histories.get(0).getMessage()).getMethodId());
		assertEquals(InvokeMethod.class.getSimpleName(), histories.get(1).getMessageType());
		assertEquals(1, ((InvokeMethod)histories.get(1).getMessage()).getMethodId());

		// 2つめのpeerがログインするとオブジェクト履歴2つのEnterRoomAllowedを受け取る
		var peer2 = new MockPeer("peer1", room);
		peer2.peerArriveAndLoginRoom();
		assertEquals("EnterRoomAllowed", peer2.getSentMessages().get(0).getType());
		var hist = ((EnterRoomAllowed)peer2.getSentMessageAt(0)).getHistories();
		assertEquals(2, hist.size());
		assertEquals("InvokeMethod", ((Map)hist.get(0)).get("type"));
		assertEquals(1, ((Map)hist.get(0)).get("methodId"));
		assertEquals("InvokeMethod", ((Map)hist.get(1)).get("type"));
		assertEquals(1, ((Map)hist.get(1)).get("methodId"));
	}

	/**
	 * InvokeMethodを送った後にUpdateObjectState(revision=1)を受け取ると正常に処理される。
	 * InvokeMethodを受け取った際にはseqNoに1が設定されていることも確かめる。
	 * @throws Throwable
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_invokeThenUpdateObject() throws Throwable{
		var room = new DefaultRoom("room1", null, null, new NullRoomEventLogger());
		var oid = 0;
		var mid = 0;
		var defObj = new DefineObject(new ObjectDefinition(
				oid, "Test", new ShareClassConfig(), Arrays.asList(
						new MethodDefinition(mid, "foo", new ShareConfig(SharingType.beforeExec, 1000))
				)));

		var peer1 = new MockPeer("peer1", room);
		peer1.peerArriveAndLoginRoom();
		peer1.peerMessage(defObj);
		peer1.peerMessage(new InvokeMethod(oid, 0, mid, new Object[] {}));
		// peer1がInvokeMethodを受け取ると、oidのオブジェクトのrevisionは1になる
		peer1.peerMessage(new UpdateObjectState(oid, null, 1));

		var peer2 = new MockPeer("peer2", room);
		peer2.peerArriveAndLoginRoom();
		peer2.peerMessage(defObj);

		// peer1が受け取るのは EnterRoomAllowed, InvokeMethod, PeerEntered の2つ
		assertEquals(3, peer1.getSentMessageCount());
		assertEquals(EnterRoomAllowed.class.getSimpleName(), peer1.getSentMessageAt(0).getType());
		assertEquals(InvokeMethod.class.getSimpleName(), peer1.getSentMessageAt(1).getType());
		assertEquals(PeerEntered.class.getSimpleName(), peer1.getSentMessageAt(2).getType());
		// peer2が受け取るのは EnterRoomAllowed のみ
		assertEquals(1, peer2.getSentMessageCount());
		assertEquals(EnterRoomAllowed.class.getSimpleName(), peer2.getSentMessageAt(0).getType());
		if(peer2.getSentMessages().get(0) instanceof EnterRoomAllowed er) {
			assertEquals(1, er.getHistories().size());
			assertEquals("UpdateObjectState", ((Map<String, Object>)er.getHistories().get(0)).get("type"));
		} else {
			fail();
		}
	}

	/*
	 * UpdateObjectStateが送られてきたら、不要になった履歴がクリアされるはず。
	 * 不要かどうかはUpdateObjectStateのrevisionで判断され、既存のUpdateObjectStateと、revisionと
	 * 同じが小さいseqNoを持つInvokeMethodが消去される。
	 * revisionより大きいseqNoを持つInvokeMethodが存在する場合、その直前にUpdateObjectStateが挿入される。
	 */
	@Test
	public void test_eliminateLogs() throws Throwable{
		var room = new DefaultRoom("room1", null, null, new NullRoomEventLogger());
		var oid = 0;
		var mid = 0;
		var defObj = new DefineObject(new ObjectDefinition(
				oid, "Test", new ShareClassConfig(), Arrays.asList(
						new MethodDefinition(mid, "foo", new ShareConfig(SharingType.beforeExec, 1000))
				)));
		var peer1 = new MockPeer("peer1", room);
		peer1.peerArriveAndLoginRoom();
		peer1.peerMessage(defObj);

		var peer2 = new MockPeer("peer2", room);
		peer2.peerArriveAndLoginRoom();
		peer2.peerMessage(defObj);

		// Peer2がInvokeMethodを送信
		peer2.peerMessage(new InvokeMethod(oid, 0, mid, new Object[] {}));
		// Peer1がInvokeMethodを送信。まだpeer2のInvokeMethodは受け取っていない想定。
		peer1.peerMessage(new InvokeMethod(oid, 0, mid, new Object[] {}));
		// Peer2がInvokeMethodを送信。まだpeer1のInvokeMethodは受け取っていない想定。
		peer2.peerMessage(new InvokeMethod(oid, 1, mid, new Object[] {}));
		assertEquals(3, room.getMessageHistorySize());
		// Peer1がUpdateObjectState送信。peer2のInvokeMethodを1つだけ受け取り、自身のInvokeMethodを送っているのでobjRevは2。
		peer1.peerMessage(new UpdateObjectState(oid, null, 2));

		// Roomの履歴を検証する。
		// InvokeMethodのseqNoは2になり、UpdateObjectStateのrevisionは1なので、2番目のInvokeMethodは削除されないはず。
		assertEquals(2, room.getMessageHistorySize());
		// Peer1のUpdateObjectStateが先頭に来る。
		assertEquals(UpdateObjectState.class.getSimpleName(), room.getMessageHistoryAt(0).getMessageType());
		assertEquals("peer1", room.getMessageHistoryAt(0).getSender());
		// Peer2のInvokeMethodがその次
		assertEquals(InvokeMethod.class.getSimpleName(), room.getMessageHistoryAt(1).getMessageType());
		assertEquals("peer2", room.getMessageHistoryAt(1).getSender());

		// Roomのオブジェクト情報を検証する
		// 最後に受け取ったUpdateObjectStateのリビジョンは1
		assertEquals(2, room.getObjectRuntimeInfos().get(oid).getLastRecvUosObjRevision());
		// InvokeMethod用のリビジョン(受け取るたびに+1)は2
		assertEquals(3, room.getObjectRuntimeInfos().get(oid).getLastSentImServerObjRevision());
	}

	/*
	 * ObjectStateが送られてきたら，それまでのInvocationのログがクリアされるはず。
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void test_eliminateLogs2() throws Throwable{
		var room = new DefaultRoom("room1", new RoomSpec(), null, new NullRoomEventLogger());
		var peer1 = new MockPeer("peer1", room);
		var peer2 = new MockPeer("peer2", room);

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
		peer1.peerMessage(new InvokeMethod(1, 0, 2, new Object[] {}));
		assertEquals(3, room.getMessageHistories().size());
		assertEquals(1, room.getObjectRuntimeInfos().get(0).getMethods().get(0).getInvocationCount());
		assertEquals(1, room.getObjectRuntimeInfos().get(0).getMethods().get(1).getInvocationCount());
		assertEquals(1, room.getObjectRuntimeInfos().get(1).getMethods().get(2).getInvocationCount());

		peer1.peerMessage(new UpdateObjectState(0, "", 2));
		assertEquals(1, room.getObjectRuntimeInfos().get(0).getMethods().get(0).getInvocationCount());
		assertEquals(1, room.getObjectRuntimeInfos().get(0).getMethods().get(1).getInvocationCount());
		assertEquals(1, room.getObjectRuntimeInfos().get(1).getMethods().get(2).getInvocationCount());

		peer2.peerArriveAndLoginRoom();
		if(peer2.getSentMessages().get(0) instanceof EnterRoomAllowed) {
			EnterRoomAllowed er = (EnterRoomAllowed)peer2.getSentMessages().get(0);
			assertEquals(2, er.getHistories().size());
			assertEquals(1, ((Map<String, Object>)er.getHistories().get(0)).get("objId"));
			assertEquals("InvokeMethod", ((Map<String, Object>)er.getHistories().get(0)).get("type"));
			assertEquals(0, ((Map<String, Object>)er.getHistories().get(1)).get("objId"));
			assertEquals("UpdateObjectState", ((Map<String, Object>)er.getHistories().get(1)).get("type"));
		} else {
			fail();
		}
	}

	@Test
	public void test_peerLeaveOnIOE() throws Throwable{
		var el = new OnMemoryEventLogger();
		var room = new DefaultRoom("room1", null, null, el);
		var peer1 = new MockPeer("peer1", room);
		var peer2 = new MockPeer("peer2", room);

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
			assertEquals("peerOpen", events.get(0)[0]);
			assertEquals("messageCast", events.get(1)[0]);
			assertEquals("peerOpen", events.get(2)[0]);
			assertEquals("messageCast", events.get(3)[0]);
			assertEquals("peerMessage", events.get(4)[0]);
			assertEquals("peerMessage", events.get(5)[0]);
			assertEquals("messageCast", events.get(6)[0]);
			assertEquals("peerMessage", events.get(7)[0]);
			assertEquals("messageCast", events.get(8)[0]);
			assertEquals(InvokeMethod.class.getSimpleName(), events.get(8)[4]);
			assertEquals("messageCast", events.get(9)[0]);
			assertEquals("PeerLeave", events.get(9)[4]);
		}
	}
}
