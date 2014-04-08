package libbun.ast;

import libbun.parser.BToken;
import libbun.parser.BVisitor;


public abstract class LocalDefinedNode extends BNode {

	public LocalDefinedNode(BNode ParentNode, BToken SourceToken, int Size) {
		super(ParentNode, SourceToken, Size);
	}

	@Override public final void Accept(BVisitor Visitor) {
		Visitor.VisitLocalDefinedNode(this);
	}

}
