package libbun.ast.unary;

import libbun.ast.BNode;
import libbun.parser.BOperatorVisitor;
import libbun.parser.BToken;
import libbun.parser.BVisitor;

public class BunPlusNode extends BUnaryNode {
	public BunPlusNode(BNode ParentNode, BToken Token) {
		super(ParentNode, Token);
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
