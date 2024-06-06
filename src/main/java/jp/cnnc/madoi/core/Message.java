package jp.cnnc.madoi.core;

public class Message {
	public Message(){
		this.type = getClass().getSimpleName();
		this.sender = "__SERVER__";
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

	public CastType getCastType() {
		return castType;
	}

	public void setCastType(CastType castType) {
		this.castType = castType;
	}

	public String[] getRecipients() {
		return recipients;
	}

	public void setRecipients(String[] recipients) {
		this.recipients = recipients;
	}

	private String type;
	private String sender;
	private CastType castType;
	private String[] recipients;
}
