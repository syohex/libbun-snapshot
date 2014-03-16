package libbun.parser.ast;

import libbun.parser.ZNameSpace;
import libbun.type.ZFuncType;
import libbun.type.ZType;
import libbun.util.Var;

public class ZAsmMacroNode extends ZTopLevelNode {
	public final static int _Macro = 0;
	public static final int _NameInfo = 1;
	public static final int _TypeInfo = 2;

	public ZAsmMacroNode(ZNode ParentNode) {
		super(ParentNode, null, 3);
	}

	public final ZType MacroType() {
		if(this.AST[ZAsmMacroNode._TypeInfo] != null) {
			return this.AST[ZAsmMacroNode._TypeInfo].Type;
		}
		return ZType.VoidType;
	}

	public final String GetName() {
		return this.AST[ZAsmMacroNode._NameInfo].SourceToken.GetTextAsName();
	}

	public final String GetMacroText() {
		@Var ZNode Node = this.AST[ZAsmNode._Macro];
		if(Node instanceof ZStringNode) {
			return ((ZStringNode)Node).StringValue;
		}
		return "";
	}

	@Override public final void Perform(ZNameSpace NameSpace) {
		@Var String MacroText = this.GetMacroText();
		@Var ZType MacroType = this.MacroType();
		if(MacroType instanceof ZFuncType) {
			NameSpace.Generator.SetAsmMacro(NameSpace, this.GetName(), (ZFuncType)MacroType, MacroText);
		}
		else {
			NameSpace.Generator.SetAsmSymbol(NameSpace, this);
		}
	}


}
