package libbun.ast;

import libbun.parser.BVisitor;
import libbun.type.BType;
import libbun.util.BField;
import libbun.util.Var;

public class BAsmNode extends BNode {
	public final static int _Macro = 0;
	public static final int _TypeInfo = 1;
	@BField public String RequiredLibrary = null;
	
	@BField String MacroText = null;
	@BField BType  MacroType = null;

	public BAsmNode(BNode ParentNode, String LibName, String MacroText, BType MacroType) {
		super(ParentNode, null, 2);
		this.RequiredLibrary = LibName;
		this.MacroText = MacroText;
		this.MacroType = MacroType;
	}

	public final BType MacroType() {
		if(this.MacroType == null) {
			this.MacroType = this.AST[BAsmNode._TypeInfo].Type;
		}
		return this.MacroType;
	}

	public final String GetMacroText() {
		if(this.MacroText == null) {
			@Var BNode Node = this.AST[BAsmNode._Macro];
			if(Node instanceof BStringNode) {
				this.MacroText = ((BStringNode)Node).StringValue;
			}
		}
		return this.MacroText;
	}

	@Override public void Accept(BVisitor Visitor) {
		Visitor.VisitAsmNode(this);
	}

}
