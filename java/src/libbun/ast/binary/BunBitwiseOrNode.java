package libbun.ast.binary;

import libbun.ast.BNode;
import libbun.lang.bun.BunPrecedence;
import libbun.parser.BToken;

public class BunBitwiseOrNode extends BBinaryNode {
	public BunBitwiseOrNode(BNode ParentNode, BToken SourceToken, BNode Left) {
		super(ParentNode, SourceToken, Left, BunPrecedence._CStyleBITOR);
	}
}
