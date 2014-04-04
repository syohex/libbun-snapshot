package libbun.parser;

import libbun.ast.BBlockNode;
import libbun.ast.BBreakNode;
import libbun.ast.BFunctionNode;
import libbun.ast.BIfNode;
import libbun.ast.BListNode;
import libbun.ast.BNode;
import libbun.ast.BReturnNode;
import libbun.ast.BThrowNode;
import libbun.util.Var;

public class BNodeUtils {

	public final static boolean _IsBlockBreak(BNode Node) {
		if(Node instanceof BReturnNode || Node instanceof BThrowNode || Node instanceof BBreakNode) {
			return true;
		}
		//System.out.println("@HasReturn"+ Node.getClass().getSimpleName());
		return false;
	}

	public final static boolean _HasFunctionBreak(BNode Node) {
		if(Node instanceof BReturnNode || Node instanceof BThrowNode) {
			return true;
		}
		if(Node instanceof BIfNode) {
			@Var BIfNode IfNode = (BIfNode)Node;
			if(IfNode.HasElseNode()) {
				return BNodeUtils._HasFunctionBreak(IfNode.ThenNode()) && BNodeUtils._HasFunctionBreak(IfNode.ElseNode());
			}
			return false;
		}
		if(Node instanceof BBlockNode) {
			@Var BBlockNode BlockNode = (BBlockNode)Node;
			@Var int i = 0;
			while(i < BlockNode.GetListSize()) {
				@Var BNode StmtNode = BlockNode.GetListAt(i);
				//System.out.println("i="+i +", "+ StmtNode.getClass().getSimpleName());
				if(BNodeUtils._HasFunctionBreak(StmtNode)) {
					return true;
				}
				i = i + 1;
			}
		}
		//System.out.println("@HasReturn"+ Node.getClass().getSimpleName());
		return false;
	}



	public final static BReturnNode _CheckIfSingleReturnNode(BFunctionNode Node) {
		@Var BBlockNode BlockNode = Node.BlockNode();
		if(BlockNode.GetListSize() == 1) {
			@Var BNode ReturnNode= BlockNode.AST[0];
			if(ReturnNode instanceof BReturnNode) {
				return (BReturnNode)ReturnNode;
			}
		}
		return null;
	}


	public final static int _AstIndexOf(BListNode LNode, BNode ChildNode) {
		@Var int i = 0;
		while(i < LNode.GetListSize()) {
			if(LNode.AST[i] == ChildNode) {
				return i;
			}
			i = i + 1;
		}
		return -1;
	}

	public final static void _CopyAstList(BListNode SourceListNode, int FromIndex, BListNode DestListNode) {
		@Var int i = FromIndex;
		while(i < SourceListNode.GetAstSize()) {
			DestListNode.Append(SourceListNode.AST[i]);
			i = i + 1;
		}
	}

	public final static void _MoveAstList(BListNode SourceListNode, int FromIndex, BListNode DestListNode) {
		BNodeUtils._CopyAstList(SourceListNode, FromIndex, DestListNode);
		SourceListNode.ClearListToSize(FromIndex);
	}

}
