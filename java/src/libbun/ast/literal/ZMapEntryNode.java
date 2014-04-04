package libbun.ast.literal;

import libbun.ast.BNode;
import libbun.ast.ZLocalDefinedNode;
import libbun.util.BField;

public class ZMapEntryNode extends ZLocalDefinedNode {
	public final static int _Key = 0;
	public final static int _Value = 1;
	@BField public String  Name = null;

	public ZMapEntryNode(BNode ParentNode) {
		super(ParentNode, null, 2);
	}

	public final BNode KeyNode() {
		return this.AST[ZMapEntryNode._Key];
	}

	public final BNode ValueNode() {
		return this.AST[ZMapEntryNode._Value];
	}
}
