package libbun.parser.ast;

import libbun.parser.ZMacroFunc;
import libbun.parser.ZNameSpace;
import libbun.type.ZFuncType;
import libbun.type.ZType;
import libbun.util.Var;

public class BunDefineNode extends ZTopLevelNode {
	public final static int _Macro = 0;
	public static final int _NameInfo = 1;
	public static final int _TypeInfo = 2;

	public BunDefineNode(ZNode ParentNode) {
		super(ParentNode, null, 3);
	}

	public final ZType MacroType() {
		if(this.AST[BunDefineNode._TypeInfo] != null) {
			return this.AST[BunDefineNode._TypeInfo].Type;
		}
		return ZType.VoidType;
	}

	public final String GetName() {
		return this.AST[BunDefineNode._NameInfo].SourceToken.GetTextAsName();
	}

	public final String GetMacroText() {
		@Var ZNode Node = this.AST[BunDefineNode._Macro];
		if(Node instanceof ZStringNode) {
			return ((ZStringNode)Node).StringValue;
		}
		return "";
	}

	@Override public final void Perform(ZNameSpace NameSpace) {
		@Var String Symbol = this.GetName();
		@Var String MacroText = this.GetMacroText();
		@Var ZType  MacroType = this.MacroType();
		@Var String LibName = null;
		@Var int loc = MacroText.indexOf("~");
		if(loc > 0) {
			LibName = MacroText.substring(0, loc);
		}
		if(loc >= 0) {
			MacroText = MacroText.substring(loc+1);
		}
		if(MacroType instanceof ZFuncType) {
			@Var ZFuncType MacroFuncType = (ZFuncType)MacroType;
			@Var ZMacroFunc MacroFunc = new ZMacroFunc(Symbol, MacroFuncType, LibName, MacroText);
			if(Symbol.equals("_")) {
				NameSpace.Generator.SetConverterFunc(MacroFuncType.GetRecvType(), MacroFuncType.GetReturnType(), MacroFunc);
			}
			else {
				NameSpace.Generator.SetDefinedFunc(MacroFunc);
			}
		}
		else {
			@Var ZAsmNode AsmNode = new ZAsmNode(null, LibName, MacroText, MacroType);
			AsmNode.SourceToken = this.GetAstToken(BunDefineNode._NameInfo);
			AsmNode.Type = MacroType;
			NameSpace.SetSymbol(Symbol, AsmNode);
		}
	}
}
