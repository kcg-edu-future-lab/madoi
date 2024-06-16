package jp.cnnc.madoi.core.message;

import java.util.Map;

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
	public EnterRoom(Map<String, Object> roomProfile, PeerInfo selfPeer) {
		this.roomProfile = roomProfile;
		this.selfPeer = selfPeer;
	}

	public Map<String, Object> getRoomProfile() {
		return roomProfile;
	}

	public void setRoom(Map<String, Object> roomProfile) {
		this.roomProfile = roomProfile;
	}

	public PeerInfo getSelfPeer() {
		return selfPeer;
	}

	public void setSelfPeer(PeerInfo selfPeer) {
		this.selfPeer = selfPeer;
	}

	private Map<String, Object> roomProfile;
	private PeerInfo selfPeer;
}
