package edu.kcg.futurelab.madoi.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.kcg.futurelab.madoi.core.message.EnterRoom;
import edu.kcg.futurelab.madoi.core.message.EnterRoomAllowed;
import edu.kcg.futurelab.madoi.core.message.EnterRoomDenied;
import edu.kcg.futurelab.madoi.core.message.InvokeFunction;
import edu.kcg.futurelab.madoi.core.message.InvokeMethod;
import edu.kcg.futurelab.madoi.core.message.LeaveRoomDone;
import edu.kcg.futurelab.madoi.core.message.Message;
import edu.kcg.futurelab.madoi.core.message.PeerEntered;
import edu.kcg.futurelab.madoi.core.message.PeerLeaved;
import edu.kcg.futurelab.madoi.core.message.Pong;
import edu.kcg.futurelab.madoi.core.message.UpdateObjectState;
import edu.kcg.futurelab.madoi.core.message.UpdatePeerProfile;
import edu.kcg.futurelab.madoi.core.message.UpdateRoomProfile;
import edu.kcg.futurelab.madoi.core.message.UserMessage;
import edu.kcg.futurelab.madoi.core.message.info.PeerInfo;
import edu.kcg.futurelab.madoi.core.message.info.RoomInfo;
import edu.kcg.futurelab.madoi.core.room.Connection;
import edu.kcg.futurelab.madoi.core.room.Peer;
import edu.kcg.futurelab.madoi.core.room.Room;

public class MockPeer implements Peer {
	@SuppressWarnings("serial")
	public MockPeer(String id, String name, Room room){
		this.id = id;
		this.profile = new HashMap<>(){{ put("name", name);}};
		this.room = room;
	}

	@Override
	public State getState() {
		return State.ENTERED;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public Map<String, Object> getProfile() {
		return profile;
	}

	public void setIoeOnSend(boolean ioeOnSend) {
		this.ioeOnSend = ioeOnSend;
	}

	@Override
	public Connection getConnection() {
		return new Connection() {
			@Override
			public void sendText(String text) throws IOException {
				if(ioeOnSend) throw new IOException();
				Message m = om.readValue(text, Message.class);
				switch(m.getType()) {
				case "Pong":{m = om.readValue(text, Pong.class); break;}
				case "EnterRoomAllowed":{m = om.readValue(text, EnterRoomAllowed.class); break;}
				case "EnterRoomDenied":{m = om.readValue(text, EnterRoomDenied.class); break;}
				case "LeaveRoomDone":{m = om.readValue(text, LeaveRoomDone.class); break;}
				case "UpdateRoomProfile":{m = om.readValue(text, UpdateRoomProfile.class); break;}
				case "PeerEntered":{m = om.readValue(text, PeerEntered.class); break;}
				case "PeerLeaved":{m = om.readValue(text, PeerLeaved.class); break;}
				case "UpdatePeerProfile":{m = om.readValue(text, UpdatePeerProfile.class); break;}
				case "InvokeFunction":{m = om.readValue(text, InvokeFunction.class); break;}
				case "InvokeMethod":{m = om.readValue(text, InvokeMethod.class); break;}
				case "UpdateObjectState":{m = om.readValue(text, UpdateObjectState.class); break;}
				default:{m = om.readValue(text, UserMessage.class); break;}
				}
				messages.add(m);
			}

			@Override
			public void send(Message message) throws IOException {
				sendText(om.writeValueAsString(message));
			}

			@Override
			public void close() throws IOException {
			}
		};
	}

	public void peerArriveAndLoginRoom() {
		peerArrive();
		loginRoom();
	}

	public void peerArrive() {
		room.onPeerArrive(this);
	}

	public void loginRoom() {
		room.onPeerMessage(
				this,
				toJsonString(new EnterRoom(
						new RoomInfo(null, null, room.getProfile()),
						new PeerInfo(id, order, profile))));
	}

	public void peerLeave() {
		room.onPeerLeave(this);
	}

	@SuppressWarnings("serial")
	public void updatePeerProfileChangeName(String name) {
		profile.put("name", name);
		var p = new UpdatePeerProfile(
				id,
				new HashMap<>() {{put("name", name);}},
				null);
		room.onPeerMessage(this, toJsonString(p));
	}

	public void peerMessage(Message m) {
		room.onPeerMessage(this, toJsonString(m));
	}

	public List<Message> getSentMessages() {
		return messages;
	}

	public int getSentMessageCount() {
		return messages.size();
	}

	public Message getSentMessageAt(int index) {
		return messages.get(index);
	}

	private String toJsonString(Object value) {
		try {
			return om.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private String id;
	private int order;
	private Map<String, Object> profile;
	private Room room;
	private List<Message> messages = new ArrayList<>();
	private ObjectMapper om = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private boolean ioeOnSend;
}

