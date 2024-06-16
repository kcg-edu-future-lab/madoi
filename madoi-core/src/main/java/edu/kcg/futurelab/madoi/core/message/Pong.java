package edu.kcg.futurelab.madoi.core.message;

public class Pong extends Message {
	public Object getBody() {
		return body;
	}
	public void setBody(Object body) {
		this.body = body;
	}
	private Object body;
}
