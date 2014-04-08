package libbun.parser.ssa;

import libbun.ast.BNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.ast.decl.BunVarBlockNode;
import libbun.ast.expression.SetNameNode;
import libbun.parser.BGenerator;
import libbun.type.BType;

public class NodeLib {
	static public boolean IsVariableNode(BNode Node) {
		if(Node instanceof BunLetVarNode) {
			return true;
		}
		else if(Node instanceof BunVarBlockNode) {
			return true;
		}
		else if(Node instanceof SetNameNode) {
			return true;
		}
		else if(Node instanceof PHINode) {
			return true;
		}
		return false;
	}

	static public BType GetType(BNode Node) {
		if(Node instanceof BunLetVarNode) {
			BunLetVarNode LNode = (BunLetVarNode) Node;
			return LNode.DeclType();
		}
		else if(Node instanceof BunVarBlockNode) {
			BunVarBlockNode VNode = (BunVarBlockNode) Node;
			return VNode.VarDeclNode().DeclType();
		}
		else if(Node instanceof SetNameNode) {
			SetNameNode SNode = (SetNameNode) Node;
			return SNode.ExprNode().Type;
		}
		else if(Node instanceof PHINode) {
			PHINode PNode = (PHINode) Node;
			return PNode.Type;
		}
		return BType.VarType;
	}

	static public String GetVarName(BNode Node, BGenerator Generator) {
		if(Node instanceof BunLetVarNode) {
			BunLetVarNode LNode = (BunLetVarNode) Node;
			return LNode.GetGivenName();
		}
		else if(Node instanceof BunVarBlockNode) {
			BunVarBlockNode VNode = (BunVarBlockNode) Node;
			return VNode.VarDeclNode().GetGivenName();
		}
		else if(Node instanceof SetNameNode) {
			SetNameNode SNode = (SetNameNode) Node;
			return SNode.NameNode().GetUniqueName(Generator);
		}
		else if(Node instanceof PHINode) {
			PHINode PNode = (PHINode) Node;
			return PNode.GetName();
		}
		return null;
	}

	static public int GetVarIndex(BNode Node) {
		if(Node instanceof BunLetVarNode) {
			return 0;
		}
		else if(Node instanceof BunVarBlockNode) {
			return 0;
		}
		else if(Node instanceof SetNameNode) {
			SetNameNode SNode = (SetNameNode) Node;
			return SNode.NameNode().VarIndex;
		}
		else if(Node instanceof PHINode) {
			PHINode PNode = (PHINode) Node;
			return PNode.GetVarIndex();
		}
		return -1;
	}
}