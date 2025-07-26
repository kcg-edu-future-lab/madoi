package edu.kcg.futurelab.madoi.core.room;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.kcg.futurelab.madoi.core.message.definition.ObjectDefinition;

/**
 * ルーム内のオブジェクトの情報を格納する。
 */
public class ObjectRuntimeInfo {
	private ObjectDefinition definition;
	private Object state;
	// 最新のUpdateObjectStateのリビジョン
	private int lastRecvUosObjRevision = 0;
	// 最新のInvokeMethodに設定したserverObjRevision;
	private int lastSentImServerObjRevision = 0;
	private Map<Integer, MethodRuntimeInfo> methodRuntimeInfos = new LinkedHashMap<>();
	public ObjectRuntimeInfo(ObjectDefinition definition, Object state,
			Map<Integer, MethodRuntimeInfo> methodRuntimeInfos) {
		this(definition);
		this.state = state;
		this.methodRuntimeInfos.putAll(methodRuntimeInfos);
	}
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
	public int getLastRecvUosObjRevision() {
		return lastRecvUosObjRevision;
	}
	public void setLastRecvUosObjRevision(int lastRecvUosObjRevision) {
		this.lastRecvUosObjRevision = lastRecvUosObjRevision;
	}
	public int getLastSentImServerObjRevision() {
		return lastSentImServerObjRevision;
	}
	public void setLastSentImServerObjRevision(int lastSentImServerObjRevision) {
		this.lastSentImServerObjRevision = lastSentImServerObjRevision;
	}
	public Map<Integer, MethodRuntimeInfo> getMethods(){
		return methodRuntimeInfos;
	}

	public int incrementAndGetImServerObjRevision(){
		return ++lastSentImServerObjRevision;
	}
}
