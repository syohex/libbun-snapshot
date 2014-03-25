package libbun.parser.ssa;

import libbun.parser.ast.ZLetVarNode;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZSetNameNode;
import libbun.parser.ast.ZVarBlockNode;
import libbun.type.ZType;

public class NodeLib {
	static public ZType GetType(Variable V) {
		if(V.Node instanceof ZLetVarNode) {
			ZLetVarNode LNode = (ZLetVarNode) V.Node;
			return LNode.DeclType();
		}
		else if(V.Node instanceof ZVarBlockNode) {
			ZVarBlockNode VNode = (ZVarBlockNode) V.Node;
			return VNode.VarDeclNode().DeclType();
		}
		else if(V.Node instanceof ZSetNameNode) {
			ZSetNameNode SNode = (ZSetNameNode) V.Node;
			return SNode.ExprNode().Type;
		}
		else if(V.Node instanceof PHINode) {
			PHINode PNode = (PHINode) V.Node;
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
}