package libbun.ast;

import libbun.parser.BToken;
import libbun.util.BField;

public class ZStupidCastErrorNode extends BErrorNode {
	@BField public BNode ErrorNode;
	public ZStupidCastErrorNode(BNode Node, BToken SourceToken, String ErrorMessage) {
		super(Node.ParentNode, SourceToken, ErrorMessage);
		this.ErrorNode = Node;
	}

}
