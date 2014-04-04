package libbun.lang.bun.shell;

import libbun.ast.BGetNameNode;
import libbun.ast.BNode;
import libbun.ast.BStringNode;
import libbun.ast.ZDesugarNode;
import libbun.ast.ZFuncCallNode;
import libbun.ast.ZSugarNode;
import libbun.parser.BGenerator;
import libbun.parser.BTypeChecker;
import libbun.util.BField;
import libbun.util.Var;

public class ArgumentNode extends ZSugarNode {
	public final static int _Expr = 0;
	// arg type
	public final static int _Normal = 0;
	public final static int _Substitution = 1;
	private final static String[] _funcNames = {"createCommandArg", "createSubstitutedArg"};

	@BField private final int ArgType;

	public ArgumentNode(BNode ParentNode, int ArgType) {
		super(ParentNode, null, 1);
		this.ArgType = ArgType;
	}

	public ArgumentNode(BNode ParentNode, String Value) {
		this(ParentNode, _Normal);
		this.SetNode(_Expr, new BStringNode(this, null, Value));
	}

	@Override public ZDesugarNode DeSugar(BGenerator Generator, BTypeChecker TypeChekcer) {
		@Var BNode Node = new ZFuncCallNode(this, new BGetNameNode(this, null, _funcNames[this.ArgType]));
		Node.SetNode(BNode._AppendIndex, this.AST[_Expr]);
		return new ZDesugarNode(this, Node);
	}
}
