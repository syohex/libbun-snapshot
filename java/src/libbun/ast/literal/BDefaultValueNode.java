package libbun.ast.literal;

import libbun.parser.BOperatorVisitor;
import libbun.parser.BVisitor;


public class BDefaultValueNode extends LiteralNode {
	public BDefaultValueNode() {
		super(null, null);
	}
	@Override public final void Accept(BVisitor Visitor) {
		if(Visitor instanceof BOperatorVisitor) {
			((BOperatorVisitor)Visitor).VisitDefaultValueNode(this);
		}
		else {
			Visitor.VisitLiteralNode(this);
		}
	}

}
