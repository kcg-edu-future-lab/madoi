package edu.kcg.futurelab.madoi.core.message;

public class UserMessage extends Message{
	public UserMessage() {
		setType(null); // シリアライズの過程で設定されるはず。
	}
	public UserMessage(String type, Object content) {
		setType(type);
		this.content = content;
	}

	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}

	private Object content;
}
