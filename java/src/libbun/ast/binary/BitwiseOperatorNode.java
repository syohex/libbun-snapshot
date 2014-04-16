package libbun.ast.binary;

import libbun.ast.BNode;


public abstract class BitwiseOperatorNode extends BinaryOperatorNode {
	public BitwiseOperatorNode(BNode ParentNode, int Precedence) {
		super(ParentNode, Precedence);
	}
}
