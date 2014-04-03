package libbun.util;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;

public abstract class ZMatchFunction extends ZFunction {
	public ZMatchFunction(int TypeId, String Name) {
		super(TypeId, Name);
	}
	protected ZMatchFunction() {
		super(0, null);
	}
	public abstract BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode);
}

