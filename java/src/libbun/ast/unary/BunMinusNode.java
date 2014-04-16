package libbun.ast.unary;

import libbun.ast.BNode;
import libbun.parser.BOperatorVisitor;
import libbun.parser.BVisitor;

public class BunMinusNode extends UnaryOperatorNode {
	public BunMinusNode(BNode ParentNode) {
		super(ParentNode);
	}
	@Override public BNode Dup(boolean TypedClone, BNode ParentNode) {
		return this.DupField(TypedClone, new BunMinusNode(ParentNode));
	}
	@Override public final String GetOperator() {
		return "-";
	}
	@Override public final void Accept(BVisitor Visitor) {
		if(Visitor instanceof BOperatorVisitor) {
			((BOperatorVisitor)Visitor).VisitMinusNode(this);
		}
		else {
			Visitor.VisitUnaryNode(this);
		}
	}
}
