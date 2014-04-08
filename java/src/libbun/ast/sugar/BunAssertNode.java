package libbun.ast.sugar;

import libbun.ast.AbstractListNode;
import libbun.ast.BNode;
import libbun.ast.DesugarNode;
import libbun.ast.SyntaxSugarNode;
import libbun.ast.expression.FuncCallNode;
import libbun.ast.literal.BunStringNode;
import libbun.encode.AbstractGenerator;
import libbun.parser.BTypeChecker;
import libbun.type.BFuncType;
import libbun.type.BMacroFunc;
import libbun.type.BType;
import libbun.util.Var;

public class BunAssertNode extends SyntaxSugarNode {
	public final static int _Expr = 0;

	public BunAssertNode(BNode ParentNode) {
		super(ParentNode, null, 1);
	}

	@Override public DesugarNode DeSugar(AbstractGenerator Generator, BTypeChecker TypeChecker) {
		@Var BMacroFunc Func = Generator.GetMacroFunc("assert", BType.BooleanType, 2);
		if(Func != null) {
			@Var AbstractListNode FuncNode = TypeChecker.CreateDefinedFuncCallNode(this.ParentNode, this.SourceToken, Func);
			FuncNode.Append(this.AST[BunAssertNode._Expr]);
			FuncNode.Append(new BunStringNode(FuncNode, null, this.GetSourceLocation()));
			return new DesugarNode(this, FuncNode);
		}
		else {
			@Var FuncCallNode MacroNode = TypeChecker.CreateFuncCallNode(this.ParentNode, this.SourceToken, "assert", BFuncType._FuncType);
			MacroNode.Append(this.AST[BunAssertNode._Expr]);
			return new DesugarNode(this, MacroNode);
		}
	}

}
