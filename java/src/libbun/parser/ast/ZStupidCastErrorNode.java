package libbun.parser.ast;

import libbun.parser.ZToken;
import libbun.util.BField;

public class ZStupidCastErrorNode extends ZErrorNode {
	@BField public BNode ErrorNode;
	public ZStupidCastErrorNode(BNode Node, ZToken SourceToken, String ErrorMessage) {
		super(Node.ParentNode, SourceToken, ErrorMessage);
		this.ErrorNode = Node;
	}

}
