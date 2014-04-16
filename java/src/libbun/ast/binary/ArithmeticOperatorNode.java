package libbun.ast.binary;

import libbun.ast.BNode;

public abstract class ArithmeticOperatorNode extends BinaryOperatorNode {

	public ArithmeticOperatorNode(BNode ParentNode, int Precedence) {
		super(ParentNode, Precedence);
	}

}
