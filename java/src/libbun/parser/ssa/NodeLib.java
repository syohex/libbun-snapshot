package libbun.parser.ssa;

import libbun.parser.ast.ZLetVarNode;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZSetNameNode;
import libbun.parser.ast.ZVarBlockNode;
import libbun.type.ZType;

public class NodeLib {
	static public boolean IsVariableNode(ZNode Node) {
		if(Node instanceof ZLetVarNode) {
			return true;
		}
		else if(Node instanceof ZVarBlockNode) {
			return true;
		}
		else if(Node instanceof ZSetNameNode) {
			return true;
		}
		else if(Node instanceof PHINode) {
			return true;
		}
		return false;
	}

	static public ZType GetType(ZNode Node) {
		if(Node instanceof ZLetVarNode) {
			ZLetVarNode LNode = (ZLetVarNode) Node;
			return LNode.DeclType();
		}
		else if(Node instanceof ZVarBlockNode) {
			ZVarBlockNode VNode = (ZVarBlockNode) Node;
			return VNode.VarDeclNode().DeclType();
		}
		else if(Node instanceof ZSetNameNode) {
			ZSetNameNode SNode = (ZSetNameNode) Node;
			return SNode.ExprNode().Type;
		}
		else if(Node instanceof PHINode) {
			PHINode PNode = (PHINode) Node;
			return PNode.Type;
		}
		return ZType.VarType;
	}

	static public String GetVarName(ZNode Node) {
		if(Node instanceof ZLetVarNode) {
			ZLetVarNode LNode = (ZLetVarNode) Node;
			return LNode.GetName();
		}
		else if(Node instanceof ZVarBlockNode) {
			ZVarBlockNode VNode = (ZVarBlockNode) Node;
			return VNode.VarDeclNode().GetName();
		}
		else if(Node instanceof ZSetNameNode) {
			ZSetNameNode SNode = (ZSetNameNode) Node;
			return SNode.GetName();
		}
		else if(Node instanceof PHINode) {
			PHINode PNode = (PHINode) Node;
			return PNode.GetName();
		}
		return null;
	}

	static public int GetVarIndex(ZNode Node) {
		if(Node instanceof ZLetVarNode) {
			return 0;
		}
		else if(Node instanceof ZVarBlockNode) {
			return 0;
		}
		else if(Node instanceof ZSetNameNode) {
			ZSetNameNode SNode = (ZSetNameNode) Node;
			return SNode.VarIndex;
		}
		else if(Node instanceof PHINode) {
			PHINode PNode = (PHINode) Node;
			return PNode.GetVarIndex();
		}
		return -1;
	}
}