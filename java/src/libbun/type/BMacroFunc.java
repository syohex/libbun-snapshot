package libbun.type;

import libbun.util.BField;

public class BMacroFunc extends BFunc {
	@BField public final String RequiredLibrary;
	@BField public final String MacroText;
	public BMacroFunc(String FuncName, BFuncType FuncType, String LibName, String MacroText) {
		super(0, FuncName, FuncType);
		this.MacroText = MacroText;
		this.RequiredLibrary = LibName;
	}
}
