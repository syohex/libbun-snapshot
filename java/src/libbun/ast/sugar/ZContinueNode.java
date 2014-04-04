package libbun.ast.sugar;

import libbun.ast.BBlockNode;
import libbun.ast.BNode;
import libbun.ast.BDesugarNode;
import libbun.ast.BSugarNode;
import libbun.ast.decl.ZVarBlockNode;
import libbun.ast.error.BErrorNode;
import libbun.ast.expression.BSetNameNode;
import libbun.ast.literal.BBooleanNode;
import libbun.ast.statement.BBreakNode;
import libbun.ast.statement.BWhileNode;
import libbun.parser.BGenerator;
import libbun.parser.BTypeChecker;
import libbun.type.BType;
import libbun.util.BLib;
import libbun.util.Var;

public class ZContinueNode extends BSugarNode {

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

	private BWhileNode LookupWhileNode() {
		@Var BNode Node = this;
		while(Node != null) {
			if(Node instanceof BWhileNode) {
				return (BWhileNode)Node;
			}
			Node = Node.ParentNode;
		}
		return null;
	}

	private BDesugarNode ReplaceContinue(BNode Node, ZContinueNode FirstNode, BNode[] NodeList, BDesugarNode FirstDesugarNode) {
		@Var int i = 0;
		while(i < Node.GetAstSize()) {
			@Var BNode SubNode = Node.AST[i];
			if(SubNode instanceof ZContinueNode) {
				@Var BDesugarNode DesugarNode = new BDesugarNode(SubNode, NodeList);
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

	@Override public BDesugarNode DeSugar(BGenerator Generator, BTypeChecker Typer) {
		@Var BWhileNode WhileNode = this.LookupWhileNode();
		if(WhileNode == null) {
			return new BDesugarNode(this, new BErrorNode(this.ParentNode, this.SourceToken, "continue must be inside the while statement"));
		}
		@Var BBlockNode ParentBlockNode = WhileNode.GetScopeBlockNode();
		@Var String VarName = Generator.NameUniqueSymbol("continue");
		@Var ZVarBlockNode VarNode = Generator.TypeChecker.CreateVarNode(null, VarName, BType.BooleanType, new BBooleanNode(true));
		@Var BWhileNode ContinueWhile = VarNode.SetNewWhileNode(BNode._AppendIndex, Typer);
		ContinueWhile.SetNewGetNameNode(BWhileNode._Cond, Typer, VarName, BType.BooleanType);
		@Var BBlockNode WhileBlockNode = ContinueWhile.SetNewBlockNode(BWhileNode._Block, Typer);
		WhileBlockNode.Append(new BSetNameNode(VarName, new BBooleanNode(false)));
		WhileBlockNode.Append(WhileNode);

		@Var BNode[] Nodes = null;
		if(WhileNode.HasNextNode()) {
			Nodes = BLib._NewNodeArray(3);
			Nodes[0] = new BSetNameNode(VarName, new BBooleanNode(true));
			Nodes[1] = WhileNode.NextNode();
			Nodes[2] = new BBreakNode(null);
		}
		else {
			Nodes = BLib._NewNodeArray(2);
			Nodes[0] = new BSetNameNode(VarName, new BBooleanNode(true));
			Nodes[1] = new BBreakNode(null);
		}
		ParentBlockNode.ReplaceWith(WhileNode, VarNode);
		return this.ReplaceContinue(WhileNode, this, Nodes, null);
	}

}
