package jp.cnnc.madoi.core.message;

public class Ping extends Message {
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
	private Object body;
}
