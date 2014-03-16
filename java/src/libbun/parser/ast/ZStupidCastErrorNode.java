package libbun.parser.ast;

import libbun.parser.ZToken;
import libbun.util.Field;

public class ZStupidCastErrorNode extends ZErrorNode {
	@Field public ZNode ErrorNode;
	public ZStupidCastErrorNode(ZNode Node, ZToken SourceToken, String ErrorMessage) {
		super(Node.ParentNode, SourceToken, ErrorMessage);
		this.ErrorNode = Node;
	}

}
