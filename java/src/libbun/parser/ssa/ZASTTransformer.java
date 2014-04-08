package libbun.parser.ssa;

import libbun.ast.BunBlockNode;
import libbun.ast.GroupNode;
import libbun.ast.AbstractListNode;
import libbun.ast.BNode;
import libbun.ast.SyntaxSugarNode;
import libbun.ast.ZLocalDefinedNode;
import libbun.ast.binary.BinaryOperatorNode;
import libbun.ast.binary.BInstanceOfNode;
import libbun.ast.decl.BunClassNode;
import libbun.ast.decl.BunFunctionNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.ast.decl.ZTopLevelNode;
import libbun.ast.decl.ZVarBlockNode;
import libbun.ast.error.ErrorNode;
import libbun.ast.expression.FuncCallNode;
import libbun.ast.expression.GetIndexNode;
import libbun.ast.expression.GetNameNode;
import libbun.ast.expression.GetFieldNode;
import libbun.ast.expression.BunMacroNode;
import libbun.ast.expression.MethodCallNode;
import libbun.ast.expression.NewObjectNode;
import libbun.ast.expression.SetIndexNode;
import libbun.ast.expression.SetNameNode;
import libbun.ast.expression.SetFieldNode;
import libbun.ast.literal.BunArrayLiteralNode;
import libbun.ast.literal.BunAsmNode;
import libbun.ast.literal.LiteralNode;
import libbun.ast.literal.BunMapLiteralNode;
import libbun.ast.statement.BunBreakNode;
import libbun.ast.statement.BunIfNode;
import libbun.ast.statement.BunReturnNode;
import libbun.ast.statement.BunThrowNode;
import libbun.ast.statement.BunTryNode;
import libbun.ast.statement.BunWhileNode;
import libbun.ast.unary.BunCastNode;
import libbun.ast.unary.UnaryOperatorNode;
import libbun.parser.BVisitor;
import libbun.util.Var;

public class ZASTTransformer extends BVisitor {
	private BNode TransformedValue;
	public ZASTTransformer() {
		this.TransformedValue = null;
	}

	protected void VisitBefore(BNode Node, int Index) {
	}

	protected void VisitAfter(BNode Node, int Index) {
	}

	protected void Transform(BNode Node, int Index) {
		BNode LastTransformed = this.TransformedValue;
		this.TransformedValue = Node.AST[Index];
		this.VisitBefore(Node, Index);
		Node.AST[Index].Accept(this);
		Node.SetNode(Index, this.TransformedValue);
		this.VisitAfter(Node, Index);
		this.TransformedValue = LastTransformed;
	}

	protected void VisitListNode(AbstractListNode Node) {
		@Var int i = 0;
		while(i < Node.GetListSize()) {
			this.Transform(Node, i);
			i = i + 1;
		}
	}

	@Override
	public void VisitLiteralNode(LiteralNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitArrayLiteralNode(BunArrayLiteralNode Node) {
		this.VisitListNode(Node);
	}

	@Override
	public void VisitMapLiteralNode(BunMapLiteralNode Node) {
		this.VisitListNode(Node);
	}

	//	@Override
	//	public void VisitNewArrayNode(ZNewArrayNode Node) {
	//		this.VisitListNode(Node);
	//	}

	@Override
	public void VisitNewObjectNode(NewObjectNode Node) {
		this.VisitListNode(Node);
	}

	@Override
	public void VisitGetNameNode(GetNameNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitSetNameNode(SetNameNode Node) {
		this.Transform(Node, SetNameNode._Expr);
	}

	@Override
	public void VisitGroupNode(GroupNode Node) {
		this.Transform(Node, GroupNode._Expr);
	}

	@Override
	public void VisitGetFieldNode(GetFieldNode Node) {
		this.Transform(Node, GetFieldNode._Recv);
	}

	@Override
	public void VisitSetFieldNode(SetFieldNode Node) {
		this.Transform(Node, SetFieldNode._Recv);
		this.Transform(Node, SetFieldNode._Expr);
	}

	@Override
	public void VisitGetIndexNode(GetIndexNode Node) {
		this.Transform(Node, GetIndexNode._Recv);
		this.Transform(Node, GetIndexNode._Index);
	}

	@Override
	public void VisitSetIndexNode(SetIndexNode Node) {
		this.Transform(Node, SetIndexNode._Recv);
		this.Transform(Node, SetIndexNode._Index);
		this.Transform(Node, SetIndexNode._Expr);
	}

	@Override
	public void VisitMethodCallNode(MethodCallNode Node) {
		this.Transform(Node, MethodCallNode._Recv);
		this.VisitListNode(Node);
	}

	@Override
	public void VisitFuncCallNode(FuncCallNode Node) {
		this.Transform(Node, FuncCallNode._Functor);
		this.VisitListNode(Node);
	}

	@Override
	public void VisitUnaryNode(UnaryOperatorNode Node) {
		this.Transform(Node, UnaryOperatorNode._Recv);
	}

	@Override
	public void VisitCastNode(BunCastNode Node) {
		this.Transform(Node, BunCastNode._Expr);
	}

	@Override
	public void VisitInstanceOfNode(BInstanceOfNode Node) {
		this.Transform(Node, BInstanceOfNode._Left);
	}

	@Override
	public void VisitBinaryNode(BinaryOperatorNode Node) {
		this.Transform(Node, BinaryOperatorNode._Left);
		this.Transform(Node, BinaryOperatorNode._Right);
	}

	@Override
	public void VisitBlockNode(BunBlockNode Node) {
		this.VisitListNode(Node);
	}

	@Override
	public void VisitVarBlockNode(ZVarBlockNode Node) {
		this.Transform(Node, BunLetVarNode._InitValue);
		this.VisitListNode(Node);
	}

	@Override
	public void VisitIfNode(BunIfNode Node) {
		this.Transform(Node, BunIfNode._Cond);
		this.Transform(Node, BunIfNode._Then);
		if(Node.HasElseNode()) {
			this.Transform(Node, BunIfNode._Else);
		}
	}

	@Override
	public void VisitReturnNode(BunReturnNode Node) {
		if(Node.ExprNode() != null) {
			this.Transform(Node, BunReturnNode._Expr);
		}
	}

	@Override
	public void VisitWhileNode(BunWhileNode Node) {
		this.Transform(Node, BunWhileNode._Cond);
		this.Transform(Node, BunWhileNode._Block);
	}

	@Override
	public void VisitBreakNode(BunBreakNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitThrowNode(BunThrowNode Node) {
		this.Transform(Node, BunThrowNode._Expr);
	}

	@Override
	public void VisitTryNode(BunTryNode Node) {
		this.Transform(Node, BunTryNode._Try);
		this.Transform(Node, BunTryNode._Catch);
		this.Transform(Node, BunTryNode._Finally);
	}

	//	public void VisitCatchNode(ZCatchNode Node) {
	//		this.Transform(Node, ZCatchNode._Block);
	//	}

	@Override
	public void VisitLetNode(BunLetVarNode Node) {
		this.Transform(Node, BunLetVarNode._InitValue);
	}

	@Override
	public void VisitFunctionNode(BunFunctionNode Node) {
		this.Transform(Node, BunFunctionNode._Block);
	}

	@Override
	public void VisitClassNode(BunClassNode Node) {
		this.VisitListNode(Node);
	}

	@Override
	public void VisitErrorNode(ErrorNode Node) {
		/* do nothing */
	}

	@Override
	public void EnableVisitor() {
		/* do nothing */
	}

	@Override
	public void StopVisitor() {
		/* do nothing */
	}

	@Override
	public boolean IsVisitable() {
		return false;
	}

	@Override
	public void VisitMacroNode(BunMacroNode Node) {
		this.VisitListNode(Node);
	}

	@Override
	public void VisitAsmNode(BunAsmNode Node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void VisitTopLevelNode(ZTopLevelNode Node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void VisitSyntaxSugarNode(SyntaxSugarNode Node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void VisitLocalDefinedNode(ZLocalDefinedNode Node) {
		// TODO Auto-generated method stub

	}


}
