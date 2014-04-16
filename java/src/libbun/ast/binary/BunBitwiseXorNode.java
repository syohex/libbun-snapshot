package libbun.ast.binary;

import libbun.ast.BNode;
import libbun.lang.bun.BunPrecedence;
import libbun.parser.BOperatorVisitor;
import libbun.parser.BVisitor;

public class BunBitwiseXorNode extends BitwiseOperatorNode {
	public BunBitwiseXorNode(BNode ParentNode) {
		super(ParentNode, BunPrecedence._CStyleBITXOR);
	}
	@Override public BNode Dup(boolean TypedClone, BNode ParentNode) {
		return this.DupField(TypedClone, new BunBitwiseXorNode(ParentNode));
	}
	@Override public final String GetOperator() {
		return "^";
	}
	@Override public final void Accept(BVisitor Visitor) {
		if(Visitor instanceof BOperatorVisitor) {
			((BOperatorVisitor)Visitor).VisitBitwiseXorNode(this);
		}
		else {
			Visitor.VisitBinaryNode(this);
		}
	}

}
