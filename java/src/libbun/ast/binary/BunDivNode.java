package libbun.ast.binary;

import libbun.ast.BNode;
import libbun.lang.bun.BunPrecedence;
import libbun.parser.BOperatorVisitor;
import libbun.parser.BVisitor;

public class BunDivNode extends ArithmeticOperatorNode {
	public BunDivNode(BNode ParentNode) {
		super(ParentNode, BunPrecedence._CStyleMUL);
	}
	@Override public BNode Dup(boolean TypedClone, BNode ParentNode) {
		return this.DupField(TypedClone, new BunDivNode(ParentNode));
	}
	@Override public final String GetOperator() {
		return "/";
	}
	@Override public final void Accept(BVisitor Visitor) {
		if(Visitor instanceof BOperatorVisitor) {
			((BOperatorVisitor)Visitor).VisitDivNode(this);
		}
		else {
			Visitor.VisitBinaryNode(this);
		}
	}
}
