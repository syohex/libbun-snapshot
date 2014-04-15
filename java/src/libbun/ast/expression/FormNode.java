package libbun.ast.expression;

import libbun.ast.AbstractListNode;
import libbun.ast.BNode;
import libbun.parser.BToken;
import libbun.parser.BVisitor;
import libbun.type.BFuncType;
import libbun.type.BFormFunc;
import libbun.util.BField;

public class FormNode extends AbstractListNode {
	@BField public final BFormFunc FormFunc;

	public FormNode(BNode ParentNode, BToken SourceToken, BFormFunc FormFunc) {
		super(ParentNode, SourceToken, 0);
		this.FormFunc = FormFunc;
		assert(FormFunc != null);
	}

	public final BFuncType GetFuncType() {
		return this.FormFunc.GetFuncType();
	}

	public final String GetFormText() {
		return this.FormFunc.FormText;
	}

	@Override public void Accept(BVisitor Visitor) {
		Visitor.VisitFormNode(this);
	}

}
