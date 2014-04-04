package libbun.parser.sugar;

import libbun.parser.BGenerator;
import libbun.parser.BTypeChecker;
import libbun.parser.ast.ZDesugarNode;
import libbun.parser.ast.ZFuncCallNode;
import libbun.parser.ast.ZListNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BStringNode;
import libbun.parser.ast.ZSugarNode;
import libbun.type.BFuncType;
import libbun.type.BMacroFunc;
import libbun.type.BType;
import libbun.util.Var;

public class ZAssertNode extends ZSugarNode {
	public final static int _Expr = 0;

	public ZAssertNode(BNode ParentNode) {
		super(ParentNode, null, 1);
	}

	@Override public ZDesugarNode DeSugar(BGenerator Generator, BTypeChecker TypeChecker) {
		@Var BMacroFunc Func = Generator.GetMacroFunc("assert", BType.BooleanType, 2);
		if(Func != null) {
			@Var ZListNode FuncNode = TypeChecker.CreateDefinedFuncCallNode(this.ParentNode, this.SourceToken, Func);
			FuncNode.Append(this.AST[ZAssertNode._Expr]);
			FuncNode.Append(new BStringNode(FuncNode, null, this.GetSourceLocation()));
			return new ZDesugarNode(this, FuncNode);
		}
		else {
			@Var ZFuncCallNode MacroNode = TypeChecker.CreateFuncCallNode(this.ParentNode, this.SourceToken, "assert", BFuncType._FuncType);
			MacroNode.Append(this.AST[ZAssertNode._Expr]);
			return new ZDesugarNode(this, MacroNode);
		}
	}

}
