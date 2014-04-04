package libbun.parser.ssa;

import libbun.parser.ZGenerator;
import libbun.parser.ast.BLetVarNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BSetNameNode;
import libbun.parser.ast.ZVarBlockNode;
import libbun.type.ZType;

public class NodeLib {
	static public boolean IsVariableNode(BNode Node) {
		if(Node instanceof BLetVarNode) {
			return true;
		}
		else if(Node instanceof ZVarBlockNode) {
			return true;
		}
		else if(Node instanceof BSetNameNode) {
			return true;
		}
		else if(Node instanceof PHINode) {
			return true;
		}
		return false;
	}

	static public ZType GetType(BNode Node) {
		if(Node instanceof BLetVarNode) {
			BLetVarNode LNode = (BLetVarNode) Node;
			return LNode.DeclType();
		}
		else if(Node instanceof ZVarBlockNode) {
			ZVarBlockNode VNode = (ZVarBlockNode) Node;
			return VNode.VarDeclNode().DeclType();
		}
		else if(Node instanceof BSetNameNode) {
			BSetNameNode SNode = (BSetNameNode) Node;
			return SNode.ExprNode().Type;
		}
		else if(Node instanceof PHINode) {
			PHINode PNode = (PHINode) Node;
			return PNode.Type;
		}
		return ZType.VarType;
	}

	static public String GetVarName(BNode Node, ZGenerator Generator) {
		if(Node instanceof BLetVarNode) {
			BLetVarNode LNode = (BLetVarNode) Node;
			return LNode.GetGivenName();
		}
		else if(Node instanceof ZVarBlockNode) {
			ZVarBlockNode VNode = (ZVarBlockNode) Node;
			return VNode.VarDeclNode().GetGivenName();
		}
		else if(Node instanceof BSetNameNode) {
			BSetNameNode SNode = (BSetNameNode) Node;
			return SNode.NameNode().GetUniqueName(Generator);
		}
		else if(Node instanceof PHINode) {
			PHINode PNode = (PHINode) Node;
			return PNode.GetName();
		}
		return null;
	}

	static public int GetVarIndex(BNode Node) {
		if(Node instanceof BLetVarNode) {
			return 0;
		}
		else if(Node instanceof ZVarBlockNode) {
			return 0;
		}
		else if(Node instanceof BSetNameNode) {
			BSetNameNode SNode = (BSetNameNode) Node;
			return SNode.NameNode().VarIndex;
		}
		else if(Node instanceof PHINode) {
			PHINode PNode = (PHINode) Node;
			return PNode.GetVarIndex();
		}
		return -1;
	}
}