package libbun.ast.binary;

import libbun.ast.BNode;
import libbun.lang.bun.BunPrecedence;
import libbun.parser.BOperatorVisitor;
import libbun.parser.BVisitor;

public class BunLeftShiftNode extends BitwiseOperatorNode {
	public BunLeftShiftNode(BNode ParentNode) {
		super(ParentNode, BunPrecedence._CStyleSHIFT);
	}
	@Override public BNode Dup(boolean TypedClone, BNode ParentNode) {
		return this.DupField(TypedClone, new BunLeftShiftNode(ParentNode));
	}

	@Override public final String GetOperator() {
		return "<<";
	}
	@Override public final void Accept(BVisitor Visitor) {
		if(Visitor instanceof BOperatorVisitor) {
			((BOperatorVisitor)Visitor).VisitLeftShiftNode(this);
		}
		else {
			Visitor.VisitBinaryNode(this);
		}
	}
}
