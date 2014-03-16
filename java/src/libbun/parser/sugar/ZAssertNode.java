package libbun.parser.sugar;

import libbun.parser.ZGenerator;
import libbun.parser.ZMacroFunc;
import libbun.parser.ZTypeChecker;
import libbun.parser.ast.ZDesugarNode;
import libbun.parser.ast.ZFuncCallNode;
import libbun.parser.ast.ZListNode;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZStringNode;
import libbun.parser.ast.ZSugarNode;
import libbun.type.ZFuncType;
import libbun.type.ZType;
import libbun.util.Var;

public class ZAssertNode extends ZSugarNode {
	public final static int _Expr = 0;

	public ZAssertNode(ZNode ParentNode) {
		super(ParentNode, null, 1);
	}

	@Override public ZDesugarNode DeSugar(ZGenerator Generator, ZTypeChecker TypeChecker) {
		@Var ZMacroFunc Func = Generator.GetMacroFunc("assert", ZType.BooleanType, 2);
		if(Func != null) {
			@Var ZListNode FuncNode = TypeChecker.CreateDefinedFuncCallNode(this.ParentNode, this.SourceToken, Func);
			FuncNode.Append(this.AST[ZAssertNode._Expr]);
			FuncNode.Append(new ZStringNode(FuncNode, null, this.GetSourceLocation()));
			return new ZDesugarNode(this, FuncNode);
		}
		else {
			@Var ZFuncCallNode MacroNode = TypeChecker.CreateFuncCallNode(this.ParentNode, this.SourceToken, "assert", ZFuncType._FuncType);
			MacroNode.Append(this.AST[ZAssertNode._Expr]);
			return new ZDesugarNode(this, MacroNode);
		}
	}

}
