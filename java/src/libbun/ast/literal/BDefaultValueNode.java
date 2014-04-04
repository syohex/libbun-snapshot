package libbun.ast.literal;

import libbun.ast.BNode;
import libbun.parser.BVisitor;


public class BDefaultValueNode extends BNode {

	public BDefaultValueNode() {
		super(null, null, 0);
	}

	@Override public final void Accept(BVisitor Visitor) {
		Visitor.VisitDefaultValueNode(this);
	}

}
