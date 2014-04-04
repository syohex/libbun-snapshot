package libbun.parser.ssa;

import libbun.ast.BArrayLiteralNode;
import libbun.ast.BAsmNode;
import libbun.ast.BBlockNode;
import libbun.ast.BBooleanNode;
import libbun.ast.BBreakNode;
import libbun.ast.BCastNode;
import libbun.ast.BDefaultValueNode;
import libbun.ast.BErrorNode;
import libbun.ast.BFloatNode;
import libbun.ast.BFunctionNode;
import libbun.ast.BGetIndexNode;
import libbun.ast.BGetNameNode;
import libbun.ast.BGetterNode;
import libbun.ast.BGroupNode;
import libbun.ast.BIfNode;
import libbun.ast.BIntNode;
import libbun.ast.BLetVarNode;
import libbun.ast.BListNode;
import libbun.ast.BNewObjectNode;
import libbun.ast.BNode;
import libbun.ast.BNullNode;
import libbun.ast.BReturnNode;
import libbun.ast.BSetIndexNode;
import libbun.ast.BSetNameNode;
import libbun.ast.BSetterNode;
import libbun.ast.BStringNode;
import libbun.ast.BThrowNode;
import libbun.ast.BTryNode;
import libbun.ast.BWhileNode;
import libbun.ast.ZClassNode;
import libbun.ast.ZFuncCallNode;
import libbun.ast.ZInstanceOfNode;
import libbun.ast.ZLocalDefinedNode;
import libbun.ast.ZMacroNode;
import libbun.ast.ZMapLiteralNode;
import libbun.ast.ZMethodCallNode;
import libbun.ast.ZSugarNode;
import libbun.ast.ZTopLevelNode;
import libbun.ast.ZVarBlockNode;
import libbun.ast.binary.BBinaryNode;
import libbun.ast.binary.BNotNode;
import libbun.ast.binary.BOrNode;
import libbun.ast.binary.BUnaryNode;
import libbun.ast.binary.BAndNode;
import libbun.ast.binary.ZComparatorNode;
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

	protected void VisitListNode(BListNode Node) {
		@Var int i = 0;
		while(i < Node.GetListSize()) {
			this.Transform(Node, i);
			i = i + 1;
		}
	}

	@Override
	public void VisitNullNode(BNullNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitBooleanNode(BBooleanNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitIntNode(BIntNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitFloatNode(BFloatNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitStringNode(BStringNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitArrayLiteralNode(BArrayLiteralNode Node) {
		this.VisitListNode(Node);
	}

	@Override
	public void VisitMapLiteralNode(ZMapLiteralNode Node) {
		this.VisitListNode(Node);
	}

	//	@Override
	//	public void VisitNewArrayNode(ZNewArrayNode Node) {
	//		this.VisitListNode(Node);
	//	}

	@Override
	public void VisitNewObjectNode(BNewObjectNode Node) {
		this.VisitListNode(Node);
	}

	@Override
	public void VisitGetNameNode(BGetNameNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitSetNameNode(BSetNameNode Node) {
		this.Transform(Node, BSetNameNode._Expr);
	}

	@Override
	public void VisitGroupNode(BGroupNode Node) {
		this.Transform(Node, BGroupNode._Expr);
	}

	@Override
	public void VisitGetterNode(BGetterNode Node) {
		this.Transform(Node, BGetterNode._Recv);
	}

	@Override
	public void VisitSetterNode(BSetterNode Node) {
		this.Transform(Node, BSetterNode._Recv);
		this.Transform(Node, BSetterNode._Expr);
	}

	@Override
	public void VisitGetIndexNode(BGetIndexNode Node) {
		this.Transform(Node, BGetIndexNode._Recv);
		this.Transform(Node, BGetIndexNode._Index);
	}

	@Override
	public void VisitSetIndexNode(BSetIndexNode Node) {
		this.Transform(Node, BSetIndexNode._Recv);
		this.Transform(Node, BSetIndexNode._Index);
		this.Transform(Node, BSetIndexNode._Expr);
	}

	@Override
	public void VisitMethodCallNode(ZMethodCallNode Node) {
		this.Transform(Node, ZMethodCallNode._Recv);
		this.VisitListNode(Node);
	}

	@Override
	public void VisitFuncCallNode(ZFuncCallNode Node) {
		this.Transform(Node, ZFuncCallNode._Functor);
		this.VisitListNode(Node);
	}

	@Override
	public void VisitUnaryNode(BUnaryNode Node) {
		this.Transform(Node, BUnaryNode._Recv);
	}

	@Override
	public void VisitNotNode(BNotNode Node) {
		this.Transform(Node, BNotNode._Recv);
	}

	@Override
	public void VisitCastNode(BCastNode Node) {
		this.Transform(Node, BCastNode._Expr);
	}

	@Override
	public void VisitInstanceOfNode(ZInstanceOfNode Node) {
		this.Transform(Node, ZInstanceOfNode._Left);
	}

	@Override
	public void VisitBinaryNode(BBinaryNode Node) {
		this.Transform(Node, BBinaryNode._Left);
		this.Transform(Node, BBinaryNode._Right);
	}

	@Override
	public void VisitComparatorNode(ZComparatorNode Node) {
		this.Transform(Node, ZComparatorNode._Left);
		this.Transform(Node, ZComparatorNode._Right);
	}

	@Override
	public void VisitAndNode(BAndNode Node) {
		this.Transform(Node, BAndNode._Left);
		this.Transform(Node, BAndNode._Right);
	}

	@Override
	public void VisitOrNode(BOrNode Node) {
		this.Transform(Node, BOrNode._Left);
		this.Transform(Node, BOrNode._Right);
	}

	@Override
	public void VisitBlockNode(BBlockNode Node) {
		this.VisitListNode(Node);
	}

	@Override
	public void VisitVarBlockNode(ZVarBlockNode Node) {
		this.Transform(Node, BLetVarNode._InitValue);
		this.VisitListNode(Node);
	}

	@Override
	public void VisitIfNode(BIfNode Node) {
		this.Transform(Node, BIfNode._Cond);
		this.Transform(Node, BIfNode._Then);
		if(Node.HasElseNode()) {
			this.Transform(Node, BIfNode._Else);
		}
	}

	@Override
	public void VisitReturnNode(BReturnNode Node) {
		if(Node.ExprNode() != null) {
			this.Transform(Node, BReturnNode._Expr);
		}
	}

	@Override
	public void VisitWhileNode(BWhileNode Node) {
		this.Transform(Node, BWhileNode._Cond);
		this.Transform(Node, BWhileNode._Block);
	}

	@Override
	public void VisitBreakNode(BBreakNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitThrowNode(BThrowNode Node) {
		this.Transform(Node, BThrowNode._Expr);
	}

	@Override
	public void VisitTryNode(BTryNode Node) {
		this.Transform(Node, BTryNode._Try);
		this.Transform(Node, BTryNode._Catch);
		this.Transform(Node, BTryNode._Finally);
	}

	//	public void VisitCatchNode(ZCatchNode Node) {
	//		this.Transform(Node, ZCatchNode._Block);
	//	}

	@Override
	public void VisitLetNode(BLetVarNode Node) {
		this.Transform(Node, BLetVarNode._InitValue);
	}

	@Override
	public void VisitFunctionNode(BFunctionNode Node) {
		this.Transform(Node, BFunctionNode._Block);
	}

	@Override
	public void VisitClassNode(ZClassNode Node) {
		this.VisitListNode(Node);
	}

	@Override
	public void VisitErrorNode(BErrorNode Node) {
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
	public void VisitMacroNode(ZMacroNode Node) {
		this.VisitListNode(Node);
	}

	@Override
	public void VisitAsmNode(BAsmNode Node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void VisitTopLevelNode(ZTopLevelNode Node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void VisitSugarNode(ZSugarNode Node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void VisitLocalDefinedNode(ZLocalDefinedNode Node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void VisitDefaultValueNode(BDefaultValueNode Node) {
		// TODO Auto-generated method stub

	}

}
