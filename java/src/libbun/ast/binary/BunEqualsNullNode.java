package libbun.ast.binary;

import libbun.ast.BNode;
import libbun.parser.BToken;

public class BunEqualsNullNode extends BunEqualsNode {
	/* Expr == null */
	public BunEqualsNullNode(BNode ParentNode, BToken SourceToken, BNode Left) {
		super(ParentNode, SourceToken, Left);
	}
}
