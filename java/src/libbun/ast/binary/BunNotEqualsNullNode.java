package libbun.ast.binary;

import libbun.ast.BNode;
import libbun.parser.BToken;

public class BunNotEqualsNullNode extends BunNotEqualsNode {
	public BunNotEqualsNullNode(BNode ParentNode, BToken SourceToken, BNode Left) {
		super(ParentNode, SourceToken, Left);
	}
}
