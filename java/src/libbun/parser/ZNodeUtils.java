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

public class ZNodeUtils {

	//	private boolean HasReturnStatement(ZNode Node) {
	//		if(Node instanceof ZBlockNode) {
	//			@Var ZBlockNode BlockNode = (ZBlockNode)Node;
	//			@Var int i = 0;
	//			@Var ZNode StmtNode = null;
	//			while(i < BlockNode.GetListSize()) {
	//				StmtNode = BlockNode.GetListAt(i);
	//				//System.out.println("i="+i +", "+ StmtNode.getClass().getSimpleName());
	//				if(ZNodeUtils._IsBreakBlock(StmtNode)) {
	//					return true;
	//				}
	//				i = i + 1;
	//			}
	//			Node = StmtNode;
	//		}
	//		if(Node instanceof ZIfNode) {
	//			@Var ZIfNode IfNode = (ZIfNode)Node;
	//			if(IfNode.HasElseNode()) {
	//				return this.HasReturnStatement(IfNode.ThenNode()) && this.HasReturnStatement(IfNode.ElseNode());
	//			}
	//			return false;
	//		}
	//		return ZNodeUtils._IsBreakBlock(Node);
	//	}

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
				return ZNodeUtils._HasFunctionBreak(IfNode.ThenNode()) && ZNodeUtils._HasFunctionBreak(IfNode.ElseNode());
			}
			return false;
		}
		if(Node instanceof ZBlockNode) {
			@Var ZBlockNode BlockNode = (ZBlockNode)Node;
			@Var int i = 0;
			while(i < BlockNode.GetListSize()) {
				@Var BNode StmtNode = BlockNode.GetListAt(i);
				//System.out.println("i="+i +", "+ StmtNode.getClass().getSimpleName());
				if(ZNodeUtils._HasFunctionBreak(StmtNode)) {
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
		ZNodeUtils._CopyAstList(SourceListNode, FromIndex, DestListNode);
		SourceListNode.ClearListToSize(FromIndex);
	}

}
