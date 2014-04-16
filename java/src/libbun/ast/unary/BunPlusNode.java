package libbun.ast.unary;

import libbun.ast.BNode;
import libbun.parser.BOperatorVisitor;
import libbun.parser.BVisitor;

public class BunPlusNode extends UnaryOperatorNode {
	public BunPlusNode(BNode ParentNode) {
		super(ParentNode);
	}
	@Override public BNode Dup(boolean TypedClone, BNode ParentNode) {
		return this.DupField(TypedClone, new BunPlusNode(ParentNode));
	}

	@Override public final String GetOperator() {
		return "+";
	}
	@Override public final void Accept(BVisitor Visitor) {
		if(Visitor instanceof BOperatorVisitor) {
			((BOperatorVisitor)Visitor).VisitPlusNode(this);
		}
		else {
			Visitor.VisitUnaryNode(this);
		}
	}
}
