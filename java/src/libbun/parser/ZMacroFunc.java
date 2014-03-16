package libbun.parser;

import libbun.type.ZFunc;
import libbun.type.ZFuncType;
import libbun.util.Field;

public class ZMacroFunc extends ZFunc {
	@Field public String RequiredLibrary = null;
	@Field public String MacroText;
	public ZMacroFunc(String FuncName, ZFuncType FuncType, String MacroText) {
		super(0, FuncName, FuncType);
		this.MacroText = MacroText;
	}
}
