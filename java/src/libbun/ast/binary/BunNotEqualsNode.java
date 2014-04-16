package libbun.ast.binary;

import libbun.ast.BNode;
import libbun.lang.bun.BunPrecedence;
import libbun.parser.BOperatorVisitor;
import libbun.parser.BToken;
import libbun.parser.BVisitor;

public class BunNotEqualsNode extends ComparatorNode {

	public BunNotEqualsNode(BNode ParentNode) {
		super(ParentNode, BunPrecedence._CStyleEquals);
	}
	@Override public BNode Dup(boolean TypedClone, BNode ParentNode) {
		return this.DupField(TypedClone, new BunNotEqualsNode(ParentNode));
	}

	@Override public final String GetOperator() {
		return "!=";
	}
	@Override public final void Accept(BVisitor Visitor) {
		if(Visitor instanceof BOperatorVisitor) {
			((BOperatorVisitor)Visitor).VisitNotEqualsNode(this);
		}
		else {
			Visitor.VisitBinaryNode(this);
		}
	}
}
