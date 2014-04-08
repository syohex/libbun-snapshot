package libbun.lang.bun.shell;

import libbun.ast.BNode;
import libbun.ast.BDesugarNode;
import libbun.ast.SyntaxSugarNode;
import libbun.ast.expression.FuncCallNode;
import libbun.ast.expression.GetNameNode;
import libbun.ast.literal.BunStringNode;
import libbun.parser.BGenerator;
import libbun.parser.BTypeChecker;
import libbun.util.BField;
import libbun.util.Var;

public class ArgumentNode extends SyntaxSugarNode {
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
		this.SetNode(_Expr, new BunStringNode(this, null, Value));
	}

	@Override public BDesugarNode DeSugar(BGenerator Generator, BTypeChecker TypeChekcer) {
		@Var BNode Node = new FuncCallNode(this, new GetNameNode(this, null, _funcNames[this.ArgType]));
		Node.SetNode(BNode._AppendIndex, this.AST[_Expr]);
		return new BDesugarNode(this, Node);
	}
}
