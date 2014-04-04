package libbun.parser.sugar;

import libbun.parser.ZGenerator;
import libbun.parser.ZTypeChecker;
import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.BBooleanNode;
import libbun.parser.ast.ZBreakNode;
import libbun.parser.ast.ZDesugarNode;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BSetNameNode;
import libbun.parser.ast.ZSugarNode;
import libbun.parser.ast.ZVarBlockNode;
import libbun.parser.ast.ZWhileNode;
import libbun.type.ZType;
import libbun.util.BLib;
import libbun.util.Var;

public class ZContinueNode extends ZSugarNode {

	public ZContinueNode(BNode ParentNode) {
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
		@Var BNode Node = this;
		while(Node != null) {
			if(Node instanceof ZWhileNode) {
				return (ZWhileNode)Node;
			}
			Node = Node.ParentNode;
		}
		return null;
	}

	private ZDesugarNode ReplaceContinue(BNode Node, ZContinueNode FirstNode, BNode[] NodeList, ZDesugarNode FirstDesugarNode) {
		@Var int i = 0;
		while(i < Node.GetAstSize()) {
			@Var BNode SubNode = Node.AST[i];
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
		@Var ZVarBlockNode VarNode = Generator.TypeChecker.CreateVarNode(null, VarName, ZType.BooleanType, new BBooleanNode(true));
		@Var ZWhileNode ContinueWhile = VarNode.SetNewWhileNode(BNode._AppendIndex, Typer);
		ContinueWhile.SetNewGetNameNode(ZWhileNode._Cond, Typer, VarName, ZType.BooleanType);
		@Var ZBlockNode WhileBlockNode = ContinueWhile.SetNewBlockNode(ZWhileNode._Block, Typer);
		WhileBlockNode.Append(new BSetNameNode(VarName, new BBooleanNode(false)));
		WhileBlockNode.Append(WhileNode);

		@Var BNode[] Nodes = null;
		if(WhileNode.HasNextNode()) {
			Nodes = BLib._NewNodeArray(3);
			Nodes[0] = new BSetNameNode(VarName, new BBooleanNode(true));
			Nodes[1] = WhileNode.NextNode();
			Nodes[2] = new ZBreakNode(null);
		}
		else {
			Nodes = BLib._NewNodeArray(2);
			Nodes[0] = new BSetNameNode(VarName, new BBooleanNode(true));
			Nodes[1] = new ZBreakNode(null);
		}
		ParentBlockNode.ReplaceWith(WhileNode, VarNode);
		return this.ReplaceContinue(WhileNode, this, Nodes, null);
	}

}
