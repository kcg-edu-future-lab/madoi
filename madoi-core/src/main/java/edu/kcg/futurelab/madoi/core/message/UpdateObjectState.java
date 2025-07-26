package edu.kcg.futurelab.madoi.core.message;

/**
 * オブジェクトの状態を通知するメッセージ。
 * オブジェクト状態は、objId, state, objRevisionから構成される。
 * objIdはオブジェクト毎に振られる連番(Madoiクライアントに登録される際に振られる)。
 * stateはオブジェクトの状態をJSON化した文字列。
 * objRevisionは変更操作が適用された回数。
 * 変更操作はMadoiサーバにより順番が固定されるため、同じobjId, objRevisionのオブジェクトのstateは同じになる。
 */
public class UpdateObjectState extends Message {
	public UpdateObjectState() {
	}
	public UpdateObjectState(int objId, Object state, int objRevision) {
		this.objId = objId;
		this.state = state;
		this.objRevision = objRevision;
	}
	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public Object getState() {
		return state;
	}
	public void setState(Object state) {
		this.state = state;
	}
	public int getObjRevision() {
		return objRevision;
	}
	public void setObjRevision(int objRevision) {
		this.objRevision = objRevision;
	}

	private int objId;
	private Object state;
	private int objRevision;
}
