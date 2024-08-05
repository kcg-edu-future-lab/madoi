package edu.kcg.futurelab.madoi.core.room;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.kcg.futurelab.madoi.core.message.definition.ObjectDefinition;

public class ObjectRuntimeInfo {
	private ObjectDefinition definition;
	private Object state;
	private int revision = 0;
	private Map<Integer, MethodRuntimeInfo> methodRuntimeInfos = new LinkedHashMap<>();
	public ObjectRuntimeInfo(ObjectDefinition definition) {
		this.definition = definition;
		var om = new ObjectMapper();
		om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
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
	public Object getState() {
		return state;
	}
	public void setState(Object state) {
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
