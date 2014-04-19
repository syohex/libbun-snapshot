package libbun.ast.binary;

import libbun.ast.BNode;
import libbun.lang.bun.BunPrecedence;
import libbun.parser.LibBunVisitor;

public class BunAssignNode extends BinaryOperatorNode {

	public BunAssignNode(BNode ParentNode) {
		super(ParentNode, BunPrecedence._CStyleAssign);
	}

	@Override public BNode Dup(boolean TypedClone, BNode ParentNode) {
		return this.DupField(TypedClone, new BunAssignNode(ParentNode));
	}

	@Override public String GetOperator() {
		return "+";
	}

	@Override public void Accept(LibBunVisitor Visitor) {
		// TODO Auto-generated method stub
	}

}
