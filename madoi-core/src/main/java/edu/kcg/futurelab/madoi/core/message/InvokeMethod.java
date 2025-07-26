package edu.kcg.futurelab.madoi.core.message;

/**
 * メソッドの実行を通知するメッセージ。
 * メソッドの実行は、objId, objRevision, methodId, args, seqNoから構成される。
 * objIdは対象のオブジェクトのID。
 * objRevisionは対象のオブジェクトのリビジョン(状態を変更するメソッドの実行の度に+1される)。
 * methodIdはメソッドのID。クライアントでオブジェクトがmadoiに登録される際に採番される。
 * argsはメソッドの引数。
 * seqNoはサーバ側で採番される、同じオブジェクトに対して実行されたメソッドの番号。
 */
public class InvokeMethod extends Message {
	public InvokeMethod(){
	}
	public InvokeMethod(int objId, int objRevision, int methodId){
		this(objId, objRevision, methodId, new Object[] {});
	}
	public InvokeMethod(int objId, int objRevision, int methodId, Object[] args){
		this(objId, objRevision, methodId, new Object[] {}, 0);
	}
		
	public InvokeMethod(int objId, int objRevision, int methodId, Object[] args, int serverObjRevision){
		this.objId = objId;
		this.objRevision = objRevision;
		this.methodId = methodId;
		this.args = args;
		this.serverObjRevision = serverObjRevision;
	}
	public int getObjId() {
		return objId;
	}
	public void setObjId(int objId) {
		this.objId = objId;
	}
	public int getObjRevision() {
		return objRevision;
	}
	public void setObjRevision(int objRevision) {
		this.objRevision = objRevision;
	}
	public int getMethodId() {
		return methodId;
	}
	public void setMethodId(int methodId) {
		this.methodId = methodId;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	public int getServerObjRevision() {
		return serverObjRevision;
	}
	public void setServerObjRevision(int serverObjRevision) {
		this.serverObjRevision = serverObjRevision;
	}
	private int objId;
	private int objRevision;
	private int methodId;
	private Object[] args;
	private int serverObjRevision;
}
