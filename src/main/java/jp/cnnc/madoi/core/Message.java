package jp.cnnc.madoi.core;

public class Message {
	public Message(){
		this.type = getClass().getSimpleName();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String type;
}
