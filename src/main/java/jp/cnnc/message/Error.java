package jp.cnnc.message;

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
