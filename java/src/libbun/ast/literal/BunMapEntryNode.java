package libbun.ast.literal;

import libbun.ast.BNode;
import libbun.ast.LocalDefinedNode;
import libbun.util.BField;

public class BunMapEntryNode extends LocalDefinedNode {
	public final static int _Key = 0;
	public final static int _Value = 1;
	@BField public String  Name = null;

	public BunMapEntryNode(BNode ParentNode) {
		super(ParentNode, null, 2);
	}

	public final BNode KeyNode() {
		return this.AST[BunMapEntryNode._Key];
	}

	public final BNode ValueNode() {
		return this.AST[BunMapEntryNode._Value];
	}
}
