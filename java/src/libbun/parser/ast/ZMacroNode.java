package libbun.parser.ast;

import libbun.parser.ZMacroFunc;
import libbun.parser.ZToken;
import libbun.parser.ZVisitor;
import libbun.type.ZFuncType;
import libbun.util.Field;

public class ZMacroNode extends ZListNode {
	@Field public final ZMacroFunc MacroFunc;

	public ZMacroNode(ZNode ParentNode, ZToken SourceToken, ZMacroFunc MacroFunc) {
		super(ParentNode, SourceToken, 0);
		this.MacroFunc = MacroFunc;
		assert(MacroFunc != null);
	}

	public final ZFuncType GetFuncType() {
		return this.MacroFunc.GetFuncType();
	}

	public final String GetMacroText() {
		return this.MacroFunc.MacroText;
	}

	@Override public void Accept(ZVisitor Visitor) {
		Visitor.VisitMacroNode(this);
	}

}
