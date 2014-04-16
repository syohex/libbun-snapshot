package libbun.ast.error;

import libbun.ast.BNode;
import libbun.parser.BToken;
import libbun.util.BField;
import libbun.util.Var;

public class StupidCastErrorNode extends ErrorNode {
	@BField public BNode ErrorNode;
	public StupidCastErrorNode(BNode Node, BToken SourceToken, String ErrorMessage) {
		super(Node.ParentNode, SourceToken, ErrorMessage);
		this.ErrorNode = Node;
	}
	@Override public BNode Dup(boolean TypedClone, BNode ParentNode) {
		if(TypedClone) {
			@Var StupidCastErrorNode NewNode = new StupidCastErrorNode(this.ErrorNode.Dup(TypedClone, ParentNode), null, this.ErrorMessage);
			NewNode.ParentNode = ParentNode;
			return this.DupField(TypedClone, NewNode);
		}
		else {
			return this.ErrorNode.Dup(TypedClone, ParentNode);
		}
	}

}
