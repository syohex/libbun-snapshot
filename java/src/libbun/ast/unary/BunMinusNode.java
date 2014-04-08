package libbun.ast.unary;

import libbun.ast.BNode;
import libbun.parser.BOperatorVisitor;
import libbun.parser.BToken;
import libbun.parser.BVisitor;

public class BunMinusNode extends BUnaryNode {
	public BunMinusNode(BNode ParentNode, BToken Token) {
		super(ParentNode, Token);
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
