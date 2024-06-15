package jp.cnnc.madoi.core.message;

/**
 */
public class EnterRoomDenied extends Message{
	public EnterRoomDenied() {
	}
	public EnterRoomDenied(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	private String message;
}
