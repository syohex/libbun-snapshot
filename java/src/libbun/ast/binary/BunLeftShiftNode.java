package libbun.ast.binary;

import libbun.ast.BNode;
import libbun.lang.bun.BunPrecedence;
import libbun.parser.BOperatorVisitor;
import libbun.parser.BToken;
import libbun.parser.BVisitor;

public class BunLeftShiftNode extends BitwiseOperatorNode {
	public BunLeftShiftNode(BNode ParentNode, BToken SourceToken, BNode Left) {
		super(ParentNode, SourceToken, Left, BunPrecedence._CStyleSHIFT);
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