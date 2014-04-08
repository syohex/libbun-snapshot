package libbun.ast.binary;

import libbun.ast.BNode;
import libbun.parser.BToken;


public class BitwiseOperatorNode extends BBinaryNode {

	public BitwiseOperatorNode(BNode ParentNode, BToken SourceToken, BNode Left, int Precedence) {
		super(ParentNode, SourceToken, Left, Precedence);
	}

}
