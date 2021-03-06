package libbun.ast.expression;

import libbun.ast.AbstractListNode;
import libbun.ast.BNode;
import libbun.parser.BToken;
import libbun.parser.LibBunVisitor;
import libbun.type.BFormFunc;
import libbun.type.BFuncType;
import libbun.util.BField;

public class BunFormNode extends AbstractListNode {
	@BField public final BFormFunc FormFunc;

	public BunFormNode(BNode ParentNode, BToken SourceToken, BFormFunc FormFunc) {
		super(ParentNode, 0);
		this.SourceToken = SourceToken;
		this.FormFunc = FormFunc;
		assert(FormFunc != null);
	}
	@Override public BNode Dup(boolean TypedClone, BNode ParentNode) {
		return this.DupField(TypedClone, new BunFormNode(ParentNode, this.SourceToken, this.FormFunc));
	}

	public final BFuncType GetFuncType() {
		return this.FormFunc.GetFuncType();
	}

	public final String GetFormText() {
		return this.FormFunc.FormText;
	}

	@Override public void Accept(LibBunVisitor Visitor) {
		Visitor.VisitFormNode(this);
	}

}
