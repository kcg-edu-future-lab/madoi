package jp.cnnc.madoi.core.message;

/**
 * メソッド実行を表すメッセージ。FunctionはobjIdがnullのメソッドとして扱う。
 */
public class InvokeFunction extends Message {
	public InvokeFunction(){
	}
	public InvokeFunction(int funcId, Object[] args){
		this.funcId = funcId;
		this.args = args;
	}
	public int getFuncId() {
		return funcId;
	}
	public void setFuncId(int funcId) {
		this.funcId = funcId;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	private int funcId;
	private Object[] args;
}
