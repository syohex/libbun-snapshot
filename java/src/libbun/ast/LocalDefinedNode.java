package libbun.ast;

import libbun.parser.BVisitor;


public abstract class LocalDefinedNode extends BNode {

	public LocalDefinedNode(BNode ParentNode, int Size) {
		super(ParentNode, Size);
	}

	@Override public final void Accept(BVisitor Visitor) {
		Visitor.VisitLocalDefinedNode(this);
	}

}
