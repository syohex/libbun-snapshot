package libbun.ast.unary;

import libbun.ast.BNode;
import libbun.parser.BOperatorVisitor;
import libbun.parser.BVisitor;

public class BunComplementNode extends UnaryOperatorNode {
	public BunComplementNode(BNode ParentNode) {
		super(ParentNode);
	}
	@Override public BNode Dup(boolean TypedClone, BNode ParentNode) {
		return this.DupField(TypedClone, new BunComplementNode(ParentNode));
	}

	@Override public final String GetOperator() {
		return "~";
	}
	@Override public final void Accept(BVisitor Visitor) {
		if(Visitor instanceof BOperatorVisitor) {
			((BOperatorVisitor)Visitor).VisitComplementNode(this);
		}
		else {
			Visitor.VisitUnaryNode(this);
		}
	}
}
