package jp.cnnc.madoi.core.room;

import java.util.HashMap;
import java.util.Map;

import jp.cnnc.madoi.core.message.definition.ObjectDefinition;

public class ObjectRuntimeInfo {
	private ObjectDefinition definition;
	private String state;
	private int revision = -1;
	private Map<Integer, MethodRuntimeInfo> methodRuntimeInfos = new HashMap<>();
	public ObjectRuntimeInfo(ObjectDefinition definition) {
		this.definition = definition;
		for(var md : definition.getMethods()) {
			var mri = new MethodRuntimeInfo(md);
			methodRuntimeInfos.put(md.getMethodId(), mri);
		}
	}
	public ObjectDefinition getDefinition() {
		return definition;
	}
	public void setDefinition(ObjectDefinition definition) {
		this.definition = definition;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getRevision() {
		return revision;
	}
	public void setRevision(int revision) {
		this.revision = revision;
	}
	public Map<Integer, MethodRuntimeInfo> getMethods(){
		return methodRuntimeInfos;
	}
}
