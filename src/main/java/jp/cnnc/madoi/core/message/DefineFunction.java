package jp.cnnc.madoi.core.message;

import jp.cnnc.madoi.core.message.definition.FunctionDefinition;

public class DefineFunction extends Message{
	public DefineFunction() {
	}
	public DefineFunction(FunctionDefinition definition) {
		this.definition = definition;
	}
	public FunctionDefinition getDefinition() {
		return definition;
	}
	public void setDefinition(FunctionDefinition definition) {
		this.definition = definition;
	}

	private FunctionDefinition definition;
}
