package libbun.parser.ast;

import libbun.parser.BToken;
import libbun.parser.BVisitor;
import libbun.type.BFuncType;
import libbun.type.BMacroFunc;
import libbun.util.BField;

public class ZMacroNode extends ZListNode {
	@BField public final BMacroFunc MacroFunc;

	public ZMacroNode(BNode ParentNode, BToken SourceToken, BMacroFunc MacroFunc) {
		super(ParentNode, SourceToken, 0);
		this.MacroFunc = MacroFunc;
		assert(MacroFunc != null);
	}

	public final BFuncType GetFuncType() {
		return this.MacroFunc.GetFuncType();
	}

	public final String GetMacroText() {
		return this.MacroFunc.MacroText;
	}

	@Override public void Accept(BVisitor Visitor) {
		Visitor.VisitMacroNode(this);
	}

}
