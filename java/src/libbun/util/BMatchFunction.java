package libbun.util;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;

public abstract class BMatchFunction extends BFunction {
	public BMatchFunction(int TypeId, String Name) {
		super(TypeId, Name);
	}
	protected BMatchFunction() {
		super(0, null);
	}
	public abstract BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode);
}

