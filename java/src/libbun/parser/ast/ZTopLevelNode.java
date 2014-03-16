package libbun.parser.ast;

import libbun.parser.ZNameSpace;
import libbun.parser.ZToken;
import libbun.parser.ZVisitor;

public abstract class ZTopLevelNode extends ZNode {

	public ZTopLevelNode(ZNode ParentNode, ZToken SourceToken, int Size) {
		super(ParentNode, SourceToken, Size);
	}

	@Override public final void Accept(ZVisitor Visitor) {
		Visitor.VisitTopLevelNode(this);
	}

	public abstract void Perform(ZNameSpace NameSpace);

}
