package libbun.parser;

import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.ZBreakNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.ZIfNode;
import libbun.parser.ast.ZListNode;
import libbun.parser.ast.ZNode;
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

	public final static boolean _IsBlockBreak(ZNode Node) {
		if(Node instanceof ZReturnNode || Node instanceof ZThrowNode || Node instanceof ZBreakNode) {
			return true;
		}
		//System.out.println("@HasReturn"+ Node.getClass().getSimpleName());
		return false;
	}

	public final static boolean _HasFunctionBreak(ZNode Node) {
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
				@Var ZNode StmtNode = BlockNode.GetListAt(i);
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
			@Var ZNode ReturnNode= BlockNode.AST[0];
			if(ReturnNode instanceof ZReturnNode) {
				return (ZReturnNode)ReturnNode;
			}
		}
		return null;
	}


	public final static int _AstIndexOf(ZListNode LNode, ZNode ChildNode) {
		@Var int i = 0;
		while(i < LNode.GetListSize()) {
			if(LNode.AST[i] == ChildNode) {
				return i;
			}
			i = i + 1;
		}
		return -1;
	}

	public final static void _CopyAstList(ZListNode sNode, int Index, ZListNode dNode) {
		@Var int i = Index;
		while(i < sNode.GetAstSize()) {
			dNode.Append(sNode.AST[i]);
			i = i + 1;
		}
	}

	public final static void _MoveAstList(ZListNode sNode, int Index, ZListNode dNode) {
		@Var int i = Index;
		while(i < sNode.GetAstSize()) {
			dNode.Append(sNode.AST[i]);
			i = i + 1;
		}
		sNode.ClearListAfter(Index);
	}

}
