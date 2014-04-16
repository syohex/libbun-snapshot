package libbun.ast.literal;

import libbun.ast.BNode;
import libbun.parser.BOperatorVisitor;
import libbun.parser.BVisitor;


public class DefaultValueNode extends LiteralNode {
	public DefaultValueNode(BNode ParentNode) {
		super(ParentNode, null);
	}
	@Override public BNode Dup(boolean TypedClone, BNode ParentNode) {
		return this.DupField(TypedClone, new DefaultValueNode(ParentNode));
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
