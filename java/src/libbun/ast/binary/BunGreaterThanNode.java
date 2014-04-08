package libbun.ast.binary;

import libbun.ast.BNode;
import libbun.lang.bun.BunPrecedence;
import libbun.parser.BOperatorVisitor;
import libbun.parser.BToken;
import libbun.parser.BVisitor;

public class BunGreaterThanNode extends ComparatorNode {

	public BunGreaterThanNode(BNode ParentNode, BToken SourceToken, BNode Left) {
		super(ParentNode, SourceToken, Left, BunPrecedence._CStyleCOMPARE);
	}
	@Override public final String GetOperator() {
		return ">";
	}
	@Override public final void Accept(BVisitor Visitor) {
		if(Visitor instanceof BOperatorVisitor) {
			((BOperatorVisitor)Visitor).VisitGreaterThanNode(this);
		}
		else {
			Visitor.VisitBinaryNode(this);
		}
	}

}
