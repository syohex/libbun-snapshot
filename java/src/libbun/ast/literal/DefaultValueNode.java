package libbun.ast.literal;

import libbun.parser.BOperatorVisitor;
import libbun.parser.BVisitor;


public class DefaultValueNode extends LiteralNode {
	public DefaultValueNode() {
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
