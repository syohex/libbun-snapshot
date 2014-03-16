package libbun.parser.sugar;

import libbun.parser.ZGenerator;
import libbun.parser.ZTypeChecker;
import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.ZBooleanNode;
import libbun.parser.ast.ZBreakNode;
import libbun.parser.ast.ZDesugarNode;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZSetNameNode;
import libbun.parser.ast.ZSugarNode;
import libbun.parser.ast.ZVarNode;
import libbun.parser.ast.ZWhileNode;
import libbun.type.ZType;
import libbun.util.LibZen;
import libbun.util.Var;

public class ZContinueNode extends ZSugarNode {

	public ZContinueNode(ZNode ParentNode) {
		super(ParentNode, null, 0);
	}

	/**
	while(EXPR) {
        A;
        continue;
	}
	==
	var continue = true;
	while(continue) {
	        continue = false;
	        while($EXPR) {
	                A;
	                continue = true;
	                break;
	        }
	}
	 **/

	private ZWhileNode LookupWhileNode() {
		@Var ZNode Node = this;
		while(Node != null) {
			if(Node instanceof ZWhileNode) {
				return (ZWhileNode)Node;
			}
			Node = Node.ParentNode;
		}
		return null;
	}

	private ZDesugarNode ReplaceContinue(ZNode Node, ZContinueNode FirstNode, ZNode[] NodeList, ZDesugarNode FirstDesugarNode) {
		@Var int i = 0;
		while(i < Node.GetAstSize()) {
			@Var ZNode SubNode = Node.AST[i];
			if(SubNode instanceof ZContinueNode) {
				@Var ZDesugarNode DesugarNode = new ZDesugarNode(SubNode, NodeList);
				if(SubNode == FirstNode) {
					FirstDesugarNode = DesugarNode;
				}
				else {
					Node.SetNode(i, DesugarNode);
				}
				break;
			}
			if(SubNode != null) {
				FirstDesugarNode = this.ReplaceContinue(SubNode, FirstNode, NodeList, FirstDesugarNode);
			}
			i = i + 1;
		}
		return FirstDesugarNode;
	}

	@Override public ZDesugarNode DeSugar(ZGenerator Generator, ZTypeChecker Typer) {
		@Var ZWhileNode WhileNode = this.LookupWhileNode();
		if(WhileNode == null) {
			return new ZDesugarNode(this, new ZErrorNode(this.ParentNode, this.SourceToken, "continue must be inside the while statement"));
		}
		@Var ZBlockNode ParentBlockNode = WhileNode.GetScopeBlockNode();
		@Var String VarName = Generator.NameUniqueSymbol("continue");
		@Var ZVarNode VarNode = Generator.TypeChecker.CreateVarNode(null, VarName, ZType.BooleanType, new ZBooleanNode(true));
		@Var ZWhileNode ContinueWhile = VarNode.SetNewWhileNode(ZNode._AppendIndex, Typer);
		ContinueWhile.SetNewGetNameNode(ZWhileNode._Cond, Typer, VarName, ZType.BooleanType);
		@Var ZBlockNode WhileBlockNode = ContinueWhile.SetNewBlockNode(ZWhileNode._Block, Typer);
		WhileBlockNode.Append(new ZSetNameNode(VarName, new ZBooleanNode(false)));
		WhileBlockNode.Append(WhileNode);

		@Var ZNode[] Nodes = null;
		if(WhileNode.HasNextNode()) {
			Nodes = LibZen._NewNodeArray(3);
			Nodes[0] = new ZSetNameNode(VarName, new ZBooleanNode(true));
			Nodes[1] = WhileNode.NextNode();
			Nodes[2] = new ZBreakNode(null);
		}
		else {
			Nodes = LibZen._NewNodeArray(2);
			Nodes[0] = new ZSetNameNode(VarName, new ZBooleanNode(true));
			Nodes[1] = new ZBreakNode(null);
		}
		ParentBlockNode.ReplaceWith(WhileNode, VarNode);
		return this.ReplaceContinue(WhileNode, this, Nodes, null);
	}

}
