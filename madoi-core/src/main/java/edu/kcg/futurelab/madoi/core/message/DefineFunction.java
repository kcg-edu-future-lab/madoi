package edu.kcg.futurelab.madoi.core.message;

import edu.kcg.futurelab.madoi.core.message.definition.FunctionDefinition;

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
