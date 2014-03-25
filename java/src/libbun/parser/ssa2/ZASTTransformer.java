package libbun.parser.ssa2;

import libbun.parser.ZVisitor;
import libbun.parser.ast.ZAndNode;
import libbun.parser.ast.ZArrayLiteralNode;
import libbun.parser.ast.ZAsmNode;
import libbun.parser.ast.ZBinaryNode;
import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.ZBooleanNode;
import libbun.parser.ast.ZBreakNode;
import libbun.parser.ast.ZCastNode;
import libbun.parser.ast.ZClassNode;
import libbun.parser.ast.ZComparatorNode;
import libbun.parser.ast.ZDefaultValueNode;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.ZFloatNode;
import libbun.parser.ast.ZFuncCallNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.ZGetIndexNode;
import libbun.parser.ast.ZGetNameNode;
import libbun.parser.ast.ZGetterNode;
import libbun.parser.ast.ZGroupNode;
import libbun.parser.ast.ZIfNode;
import libbun.parser.ast.ZInstanceOfNode;
import libbun.parser.ast.ZIntNode;
import libbun.parser.ast.ZLetVarNode;
import libbun.parser.ast.ZListNode;
import libbun.parser.ast.ZLocalDefinedNode;
import libbun.parser.ast.ZMacroNode;
import libbun.parser.ast.ZMapLiteralNode;
import libbun.parser.ast.ZMethodCallNode;
import libbun.parser.ast.ZNewObjectNode;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZNotNode;
import libbun.parser.ast.ZNullNode;
import libbun.parser.ast.ZOrNode;
import libbun.parser.ast.ZReturnNode;
import libbun.parser.ast.ZSetIndexNode;
import libbun.parser.ast.ZSetNameNode;
import libbun.parser.ast.ZSetterNode;
import libbun.parser.ast.ZStringNode;
import libbun.parser.ast.ZSugarNode;
import libbun.parser.ast.ZThrowNode;
import libbun.parser.ast.ZTopLevelNode;
import libbun.parser.ast.ZTryNode;
import libbun.parser.ast.ZUnaryNode;
import libbun.parser.ast.ZVarBlockNode;
import libbun.parser.ast.ZWhileNode;
import libbun.util.Var;

public class ZASTTransformer extends ZVisitor {
	private ZNode TransformedValue;
	public ZASTTransformer() {
		this.TransformedValue = null;
	}

	protected void VisitBefore(ZNode Node, int Index) {
	}

	protected void VisitAfter(ZNode Node, int Index) {
	}

	protected void Transform(ZNode Node, int Index) {
		ZNode LastTransformed = this.TransformedValue;
		this.TransformedValue = Node.AST[Index];
		this.VisitBefore(Node, Index);
		Node.AST[Index].Accept(this);
		Node.SetNode(Index, this.TransformedValue);
		this.VisitAfter(Node, Index);
		this.TransformedValue = LastTransformed;
	}

	protected void VisitListNode(ZListNode Node) {
		@Var int i = 0;
		while(i < Node.GetListSize()) {
			this.Transform(Node, i);
		}
	}

	@Override
	public void VisitNullNode(ZNullNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitBooleanNode(ZBooleanNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitIntNode(ZIntNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitFloatNode(ZFloatNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitStringNode(ZStringNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitArrayLiteralNode(ZArrayLiteralNode Node) {
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
	public void VisitNewObjectNode(ZNewObjectNode Node) {
		this.VisitListNode(Node);
	}

	@Override
	public void VisitGetNameNode(ZGetNameNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitSetNameNode(ZSetNameNode Node) {
		this.Transform(Node, ZSetNameNode._Expr);
	}

	@Override
	public void VisitGroupNode(ZGroupNode Node) {
		this.Transform(Node, ZGroupNode._Expr);
	}

	@Override
	public void VisitGetterNode(ZGetterNode Node) {
		this.Transform(Node, ZGetterNode._Recv);
	}

	@Override
	public void VisitSetterNode(ZSetterNode Node) {
		this.Transform(Node, ZSetterNode._Recv);
		this.Transform(Node, ZSetterNode._Expr);
	}

	@Override
	public void VisitGetIndexNode(ZGetIndexNode Node) {
		this.Transform(Node, ZGetIndexNode._Recv);
		this.Transform(Node, ZGetIndexNode._Index);
	}

	@Override
	public void VisitSetIndexNode(ZSetIndexNode Node) {
		this.Transform(Node, ZSetIndexNode._Recv);
		this.Transform(Node, ZSetIndexNode._Index);
		this.Transform(Node, ZSetIndexNode._Expr);
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
	public void VisitUnaryNode(ZUnaryNode Node) {
		this.Transform(Node, ZUnaryNode._Recv);
	}

	@Override
	public void VisitNotNode(ZNotNode Node) {
		this.Transform(Node, ZNotNode._Recv);
	}

	@Override
	public void VisitCastNode(ZCastNode Node) {
		this.Transform(Node, ZCastNode._Expr);
	}

	@Override
	public void VisitInstanceOfNode(ZInstanceOfNode Node) {
		this.Transform(Node, ZInstanceOfNode._Left);
	}

	@Override
	public void VisitBinaryNode(ZBinaryNode Node) {
		this.Transform(Node, ZBinaryNode._Left);
		this.Transform(Node, ZBinaryNode._Right);
	}

	@Override
	public void VisitComparatorNode(ZComparatorNode Node) {
		this.Transform(Node, ZComparatorNode._Left);
		this.Transform(Node, ZComparatorNode._Right);
	}

	@Override
	public void VisitAndNode(ZAndNode Node) {
		this.Transform(Node, ZAndNode._Left);
		this.Transform(Node, ZAndNode._Right);
	}

	@Override
	public void VisitOrNode(ZOrNode Node) {
		this.Transform(Node, ZOrNode._Left);
		this.Transform(Node, ZOrNode._Right);
	}

	@Override
	public void VisitBlockNode(ZBlockNode Node) {
		this.VisitListNode(Node);
	}

	@Override
	public void VisitVarBlockNode(ZVarBlockNode Node) {
		this.Transform(Node, ZLetVarNode._InitValue);
		this.VisitListNode(Node);
	}

	@Override
	public void VisitIfNode(ZIfNode Node) {
		this.Transform(Node, ZIfNode._Cond);
		this.Transform(Node, ZIfNode._Then);
		if(Node.HasElseNode()) {
			this.Transform(Node, ZIfNode._Else);
		}
	}

	@Override
	public void VisitReturnNode(ZReturnNode Node) {
		if(Node.ExprNode() != null) {
			this.Transform(Node, ZReturnNode._Expr);
		}
	}

	@Override
	public void VisitWhileNode(ZWhileNode Node) {
		this.Transform(Node, ZWhileNode._Cond);
		this.Transform(Node, ZWhileNode._Block);
	}

	@Override
	public void VisitBreakNode(ZBreakNode Node) {
		/* do nothing */
	}

	@Override
	public void VisitThrowNode(ZThrowNode Node) {
		this.Transform(Node, ZThrowNode._Expr);
	}

	@Override
	public void VisitTryNode(ZTryNode Node) {
		this.Transform(Node, ZTryNode._Try);
		this.Transform(Node, ZTryNode._Catch);
		this.Transform(Node, ZTryNode._Finally);
	}

	//	public void VisitCatchNode(ZCatchNode Node) {
	//		this.Transform(Node, ZCatchNode._Block);
	//	}

	@Override
	public void VisitLetNode(ZLetVarNode Node) {
		this.Transform(Node, ZLetVarNode._InitValue);
	}

	@Override
	public void VisitFunctionNode(ZFunctionNode Node) {
		this.Transform(Node, ZFunctionNode._Block);
	}

	@Override
	public void VisitClassNode(ZClassNode Node) {
		this.VisitListNode(Node);
	}

	@Override
	public void VisitErrorNode(ZErrorNode Node) {
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
	public void VisitAsmNode(ZAsmNode Node) {
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
	public void VisitDefaultValueNode(ZDefaultValueNode Node) {
		// TODO Auto-generated method stub

	}

}
