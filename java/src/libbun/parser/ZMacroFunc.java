package libbun.parser;

import libbun.type.ZFunc;
import libbun.type.ZFuncType;
import libbun.util.BField;

public class ZMacroFunc extends ZFunc {
	@BField public final String RequiredLibrary;
	@BField public final String MacroText;
	public ZMacroFunc(String FuncName, ZFuncType FuncType, String LibName, String MacroText) {
		super(0, FuncName, FuncType);
		this.MacroText = MacroText;
		this.RequiredLibrary = LibName;
	}
}
