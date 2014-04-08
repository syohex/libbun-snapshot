package libbun.ast.binary;

import libbun.ast.BNode;
import libbun.parser.BToken;


public abstract class BitwiseOperatorNode extends BinaryOperatorNode {
	public BitwiseOperatorNode(BNode ParentNode, BToken SourceToken, BNode Left, int Precedence) {
		super(ParentNode, SourceToken, Left, Precedence);
	}
}
