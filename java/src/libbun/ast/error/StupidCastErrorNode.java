package libbun.ast.error;

import libbun.ast.BNode;
import libbun.parser.BToken;
import libbun.util.BField;

public class StupidCastErrorNode extends ErrorNode {
	@BField public BNode ErrorNode;
	public StupidCastErrorNode(BNode Node, BToken SourceToken, String ErrorMessage) {
		super(Node.ParentNode, SourceToken, ErrorMessage);
		this.ErrorNode = Node;
	}
}
