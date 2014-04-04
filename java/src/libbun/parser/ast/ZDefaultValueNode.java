package libbun.parser.ast;

import libbun.parser.BVisitor;


public class ZDefaultValueNode extends BNode {

	public ZDefaultValueNode() {
		super(null, null, 0);
	}

	@Override public final void Accept(BVisitor Visitor) {
		Visitor.VisitDefaultValueNode(this);
	}

}
