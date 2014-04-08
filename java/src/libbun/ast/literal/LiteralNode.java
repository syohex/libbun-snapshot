package libbun.ast.literal;

import libbun.ast.BNode;
import libbun.parser.BToken;

public abstract class LiteralNode extends BConstNode {
	protected LiteralNode(BNode ParentNode, BToken SourceToken) {
		super(ParentNode, SourceToken);
	}
}
