package libbun.parser;

import libbun.parser.ast.BNode;
import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.ZBreakNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.ZIfNode;
import libbun.parser.ast.ZListNode;
import libbun.parser.ast.ZReturnNode;
import libbun.parser.ast.ZThrowNode;
import libbun.util.Var;

public class BNodeUtils {

	public final static boolean _IsBlockBreak(BNode Node) {
		if(Node instanceof ZReturnNode || Node instanceof ZThrowNode || Node instanceof ZBreakNode) {
			return true;
		}
		//System.out.println("@HasReturn"+ Node.getClass().getSimpleName());
		return false;
	}

	public final static boolean _HasFunctionBreak(BNode Node) {
		if(Node instanceof ZReturnNode || Node instanceof ZThrowNode) {
			return true;
		}
		if(Node instanceof ZIfNode) {
			@Var ZIfNode IfNode = (ZIfNode)Node;
			if(IfNode.HasElseNode()) {
				return BNodeUtils._HasFunctionBreak(IfNode.ThenNode()) && BNodeUtils._HasFunctionBreak(IfNode.ElseNode());
			}
			return false;
		}
		if(Node instanceof ZBlockNode) {
			@Var ZBlockNode BlockNode = (ZBlockNode)Node;
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



	public final static ZReturnNode _CheckIfSingleReturnNode(ZFunctionNode Node) {
		@Var ZBlockNode BlockNode = Node.BlockNode();
		if(BlockNode.GetListSize() == 1) {
			@Var BNode ReturnNode= BlockNode.AST[0];
			if(ReturnNode instanceof ZReturnNode) {
				return (ZReturnNode)ReturnNode;
			}
		}
		return null;
	}


	public final static int _AstIndexOf(ZListNode LNode, BNode ChildNode) {
		@Var int i = 0;
		while(i < LNode.GetListSize()) {
			if(LNode.AST[i] == ChildNode) {
				return i;
			}
			i = i + 1;
		}
		return -1;
	}

	public final static void _CopyAstList(ZListNode SourceListNode, int FromIndex, ZListNode DestListNode) {
		@Var int i = FromIndex;
		while(i < SourceListNode.GetAstSize()) {
			DestListNode.Append(SourceListNode.AST[i]);
			i = i + 1;
		}
	}

	public final static void _MoveAstList(ZListNode SourceListNode, int FromIndex, ZListNode DestListNode) {
		BNodeUtils._CopyAstList(SourceListNode, FromIndex, DestListNode);
		SourceListNode.ClearListToSize(FromIndex);
	}

}
