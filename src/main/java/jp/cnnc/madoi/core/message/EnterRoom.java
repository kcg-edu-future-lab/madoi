package jp.cnnc.madoi.core.message;

import java.util.Map;

import jp.cnnc.madoi.core.Message;

/**
 * ルームに入室する際にクライアントからサーバへ送るメッセージ。
 * ルームのIDとキーはURL内で指定する。
 * 例: https://madoi-server/madoi/rooms/${roomId}?key=${key}
 */
public class EnterRoom extends Message{
	public EnterRoom() {
	}

	/**
	 * コンストラクタ。
	 * @param roomSpec ルームの仕様を表す。機能やプラグインなどの要求があればここに記述する。ルーム側が要求を満たさない場合は入室に失敗する。
	 * @param peerId 自身のpeerId。自動で生成する場合はnull。
	 * @param peerProfile 名前などのprofile情報。
	 */
	public EnterRoom(Map<String, Object> roomSpec, String peerId, Map<String, Object> peerProfile) {
		super();
		this.roomSpec = roomSpec;
		this.peerId = peerId;
		this.peerProfile = peerProfile;
	}

	public Map<String, Object> getRoomSpec() {
		return roomSpec;
	}
	public void setRoomSpec(Map<String, Object> roomSpec) {
		this.roomSpec = roomSpec;
	}
	public String getPeerId() {
		return peerId;
	}
	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}
	public Map<String, Object> getPeerProfile() {
		return peerProfile;
	}
	public void setPeerProfile(Map<String, Object> peerProfile) {
		this.peerProfile = peerProfile;
	}

	private Map<String, Object> roomSpec;
	private String peerId;
	private Map<String, Object> peerProfile;
}
