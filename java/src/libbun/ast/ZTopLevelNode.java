package libbun.ast;

import libbun.parser.BNameSpace;
import libbun.parser.BToken;
import libbun.parser.BVisitor;

public abstract class ZTopLevelNode extends BNode {

	public ZTopLevelNode(BNode ParentNode, BToken SourceToken, int Size) {
		super(ParentNode, SourceToken, Size);
	}

	@Override public final void Accept(BVisitor Visitor) {
		Visitor.VisitTopLevelNode(this);
	}

	public abstract void Perform(BNameSpace NameSpace);

}
