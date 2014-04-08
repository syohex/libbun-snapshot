package libbun.ast.binary;

import libbun.ast.BNode;
import libbun.parser.BToken;

public abstract class ArithmeticOperatorNode extends BBinaryNode {

	public ArithmeticOperatorNode(BNode ParentNode, BToken SourceToken, BNode Left, int Precedence) {
		super(ParentNode, SourceToken, Left, Precedence);
	}

}
