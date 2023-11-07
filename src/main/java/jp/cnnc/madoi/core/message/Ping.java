package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.Message;

public class Ping extends Message {
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
	private Object body;
}
