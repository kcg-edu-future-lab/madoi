package jp.cnnc.madoi.core;

public class Message {
	public Message(){
		this.type = getClass().getSimpleName();
		this.sender = "__SYSTEM__";
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	private String type;
	private String sender;
}
