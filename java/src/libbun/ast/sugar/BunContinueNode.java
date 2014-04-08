package libbun.ast.sugar;

import libbun.ast.BunBlockNode;
import libbun.ast.BNode;
import libbun.ast.BDesugarNode;
import libbun.ast.SyntaxSugarNode;
import libbun.ast.decl.ZVarBlockNode;
import libbun.ast.error.ErrorNode;
import libbun.ast.expression.SetNameNode;
import libbun.ast.literal.BunBooleanNode;
import libbun.ast.statement.BunBreakNode;
import libbun.ast.statement.BunWhileNode;
import libbun.parser.BGenerator;
import libbun.parser.BTypeChecker;
import libbun.type.BType;
import libbun.util.BLib;
import libbun.util.Var;

public class BunContinueNode extends SyntaxSugarNode {

	public BunContinueNode(BNode ParentNode) {
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

	private BunWhileNode LookupWhileNode() {
		@Var BNode Node = this;
		while(Node != null) {
			if(Node instanceof BunWhileNode) {
				return (BunWhileNode)Node;
			}
			Node = Node.ParentNode;
		}
		return null;
	}

	private BDesugarNode ReplaceContinue(BNode Node, BunContinueNode FirstNode, BNode[] NodeList, BDesugarNode FirstDesugarNode) {
		@Var int i = 0;
		while(i < Node.GetAstSize()) {
			@Var BNode SubNode = Node.AST[i];
			if(SubNode instanceof BunContinueNode) {
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
		@Var BunWhileNode WhileNode = this.LookupWhileNode();
		if(WhileNode == null) {
			return new BDesugarNode(this, new ErrorNode(this.ParentNode, this.SourceToken, "continue must be inside the while statement"));
		}
		@Var BunBlockNode ParentBlockNode = WhileNode.GetScopeBlockNode();
		@Var String VarName = Generator.NameUniqueSymbol("continue");
		@Var ZVarBlockNode VarNode = Generator.TypeChecker.CreateVarNode(null, VarName, BType.BooleanType, new BunBooleanNode(true));
		@Var BunWhileNode ContinueWhile = VarNode.SetNewWhileNode(BNode._AppendIndex, Typer);
		ContinueWhile.SetNewGetNameNode(BunWhileNode._Cond, Typer, VarName, BType.BooleanType);
		@Var BunBlockNode WhileBlockNode = ContinueWhile.SetNewBlockNode(BunWhileNode._Block, Typer);
		WhileBlockNode.Append(new SetNameNode(VarName, new BunBooleanNode(false)));
		WhileBlockNode.Append(WhileNode);

		@Var BNode[] Nodes = null;
		if(WhileNode.HasNextNode()) {
			Nodes = BLib._NewNodeArray(3);
			Nodes[0] = new SetNameNode(VarName, new BunBooleanNode(true));
			Nodes[1] = WhileNode.NextNode();
			Nodes[2] = new BunBreakNode(null);
		}
		else {
			Nodes = BLib._NewNodeArray(2);
			Nodes[0] = new SetNameNode(VarName, new BunBooleanNode(true));
			Nodes[1] = new BunBreakNode(null);
		}
		ParentBlockNode.ReplaceWith(WhileNode, VarNode);
		return this.ReplaceContinue(WhileNode, this, Nodes, null);
	}

}
