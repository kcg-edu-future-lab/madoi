package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.message.definition.ObjectDefinition;

public class DefineObject extends Message{
	public DefineObject() {
	}
	public DefineObject(ObjectDefinition definition) {
		this.definition = definition;
	}

	public ObjectDefinition getDefinition() {
		return definition;
	}
	public void setDefinition(ObjectDefinition definition) {
		this.definition = definition;
	}

	private ObjectDefinition definition;
}
