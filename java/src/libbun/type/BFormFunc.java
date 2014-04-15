package libbun.type;

import libbun.util.BField;

public class BFormFunc extends BFunc {
	@BField public final String RequiredLibrary;
	@BField public final String FormText;
	public BFormFunc(String FuncName, BFuncType FuncType, String LibName, String FormText) {
		super(0, FuncName, FuncType);
		this.FormText = FormText;
		this.RequiredLibrary = LibName;
	}
}
