package libbun.parser;

import libbun.type.ZFunc;
import libbun.type.ZFuncType;
import libbun.util.Field;

public class ZMacroFunc extends ZFunc {
	@Field public final String RequiredLibrary;
	@Field public final String MacroText;
	public ZMacroFunc(String FuncName, ZFuncType FuncType, String LibName, String MacroText) {
		super(0, FuncName, FuncType);
		this.MacroText = MacroText;
		this.RequiredLibrary = LibName;
	}
}
