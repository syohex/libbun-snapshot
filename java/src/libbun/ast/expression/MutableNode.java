package libbun.ast.expression;

import libbun.ast.BNode;

public abstract class MutableNode extends BNode {
	public boolean IsImmutable = false;
	public MutableNode(BNode ParentNode, int Size) {
		super(ParentNode, Size);
	}
}
