package libbun.parser.ast;

import libbun.parser.ZMacroFunc;
import libbun.parser.ZNameSpace;
import libbun.type.ZFuncType;
import libbun.type.ZType;
import libbun.util.BField;
import libbun.util.Var;

public class BDefineNode extends ZTopLevelNode {
	@BField public BLetVarNode DefineNode;

	public BDefineNode(BNode ParentNode, BLetVarNode DefineNode) {
		super(ParentNode, null, 3);
		this.DefineNode = DefineNode;
	}

	private String GetMacroText() {
		@Var BNode Node = this.DefineNode.InitValueNode();
		if(Node instanceof BStringNode) {
			return ((BStringNode)Node).StringValue;
		}
		return "";
	}

	@Override public final void Perform(ZNameSpace NameSpace) {
		@Var String Symbol = this.DefineNode.GetGivenName();
		@Var String MacroText = this.GetMacroText();
		@Var ZType  MacroType = this.DefineNode.DeclType();
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
				//				System.out.println("Func: " + MacroFunc + " by " + MacroFunc.GetSignature());
				NameSpace.Generator.SetDefinedFunc(MacroFunc);
			}
		}
		else {
			//let symbol = asm "macro": type;
			@Var BAsmNode AsmNode = new BAsmNode(null, LibName, MacroText, MacroType);
			AsmNode.SourceToken = this.DefineNode.GetAstToken(BLetVarNode._NameInfo);
			AsmNode.Type = MacroType;
			this.DefineNode.SetNode(BLetVarNode._InitValue, AsmNode);
			NameSpace.SetSymbol(Symbol, this.DefineNode);
		}
	}
}
