package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.Message;

public class Error extends Message{
	public Error() {
	}
	public Error(String message) {
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
