package libbun.ast.binary;

import libbun.ast.BNode;
import libbun.lang.bun.BunPrecedence;
import libbun.parser.BToken;

public class BunBitwiseAndNode extends BBinaryNode {
	public BunBitwiseAndNode(BNode ParentNode, BToken SourceToken, BNode Left) {
		super(ParentNode, SourceToken, Left, BunPrecedence._CStyleBITAND);
	}
}
