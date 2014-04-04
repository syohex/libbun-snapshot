package libbun.ast.sugar;

import libbun.ast.BListNode;
import libbun.ast.BNode;
import libbun.ast.BDesugarNode;
import libbun.ast.BSugarNode;
import libbun.ast.expression.BFuncCallNode;
import libbun.ast.literal.BStringNode;
import libbun.parser.BGenerator;
import libbun.parser.BTypeChecker;
import libbun.type.BFuncType;
import libbun.type.BMacroFunc;
import libbun.type.BType;
import libbun.util.Var;

public class ZAssertNode extends BSugarNode {
	public final static int _Expr = 0;

	public ZAssertNode(BNode ParentNode) {
		super(ParentNode, null, 1);
	}

	@Override public BDesugarNode DeSugar(BGenerator Generator, BTypeChecker TypeChecker) {
		@Var BMacroFunc Func = Generator.GetMacroFunc("assert", BType.BooleanType, 2);
		if(Func != null) {
			@Var BListNode FuncNode = TypeChecker.CreateDefinedFuncCallNode(this.ParentNode, this.SourceToken, Func);
			FuncNode.Append(this.AST[ZAssertNode._Expr]);
			FuncNode.Append(new BStringNode(FuncNode, null, this.GetSourceLocation()));
			return new BDesugarNode(this, FuncNode);
		}
		else {
			@Var BFuncCallNode MacroNode = TypeChecker.CreateFuncCallNode(this.ParentNode, this.SourceToken, "assert", BFuncType._FuncType);
			MacroNode.Append(this.AST[ZAssertNode._Expr]);
			return new BDesugarNode(this, MacroNode);
		}
	}

}
