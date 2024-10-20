package edu.kcg.futurelab.madoi.core.message;

import edu.kcg.futurelab.madoi.core.message.info.PeerInfo;
import edu.kcg.futurelab.madoi.core.message.info.RoomInfo;

/**
 * ルームに入室する際にクライアントからサーバへ送るメッセージ。
 */
public class EnterRoom extends Message{
	public EnterRoom() {
	}

	/**
	 * コンストラクタ。
	 * @param roomProfile ルームのプロファイル。
	 * @param peerId 自身のpeerId。自動で生成する場合はnull。
	 * @param peerProfile 名前などのprofile情報。
	 */
	public EnterRoom(RoomInfo room, PeerInfo selfPeer) {
		this.room = room;
		this.selfPeer = selfPeer;
	}

	public RoomInfo getRoom() {
		return room;
	}

	public void setRoom(RoomInfo room) {
		this.room = room;
	}

	public PeerInfo getSelfPeer() {
		return selfPeer;
	}

	public void setSelfPeer(PeerInfo selfPeer) {
		this.selfPeer = selfPeer;
	}

	private RoomInfo room;
	private PeerInfo selfPeer;
}
