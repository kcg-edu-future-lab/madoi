package jp.cnnc.madoi.core.message;

public class CustomMessage extends Message{
	public CustomMessage() {
		setType(null); // シリアライズの過程で設定されるはず。
	}
	public CustomMessage(String type, Object body) {
		setType(type);
		this.body = body;
	}

	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}

	private Object body;
}
