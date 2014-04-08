package libbun.ast.decl;

import libbun.ast.BNode;
import libbun.ast.literal.BunAsmNode;
import libbun.ast.literal.BunStringNode;
import libbun.parser.BNameSpace;
import libbun.type.BFuncType;
import libbun.type.BMacroFunc;
import libbun.type.BType;
import libbun.util.BField;
import libbun.util.Var;

public class BunDefineNode extends TopLevelNode {
	@BField public BunLetVarNode DefineNode;

	public BunDefineNode(BNode ParentNode, BunLetVarNode DefineNode) {
		super(ParentNode, null, 3);
		this.DefineNode = DefineNode;
	}

	private String GetMacroText() {
		@Var BNode Node = this.DefineNode.InitValueNode();
		if(Node instanceof BunStringNode) {
			return ((BunStringNode)Node).StringValue;
		}
		return "";
	}

	@Override public final void Perform(BNameSpace NameSpace) {
		@Var String Symbol = this.DefineNode.GetGivenName();
		@Var String MacroText = this.GetMacroText();
		@Var BType  MacroType = this.DefineNode.DeclType();
		@Var String LibName = null;
		@Var int loc = MacroText.indexOf("~");
		if(loc > 0) {
			LibName = MacroText.substring(0, loc);
		}
		if(loc >= 0) {
			MacroText = MacroText.substring(loc+1);
		}
		if(MacroType instanceof BFuncType) {
			@Var BFuncType MacroFuncType = (BFuncType)MacroType;
			@Var BMacroFunc MacroFunc = new BMacroFunc(Symbol, MacroFuncType, LibName, MacroText);
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
			@Var BunAsmNode AsmNode = new BunAsmNode(null, LibName, MacroText, MacroType);
			AsmNode.SourceToken = this.DefineNode.GetAstToken(BunLetVarNode._NameInfo);
			AsmNode.Type = MacroType;
			this.DefineNode.SetNode(BunLetVarNode._InitValue, AsmNode);
			NameSpace.SetSymbol(Symbol, this.DefineNode);
		}
	}
}
