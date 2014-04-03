package libbun.parser.ast;

import libbun.parser.ZVisitor;
import libbun.type.ZType;
import libbun.util.Field;
import libbun.util.Var;

public class BAsmNode extends BNode {
	public final static int _Macro = 0;
	public static final int _TypeInfo = 1;
	@Field public String RequiredLibrary = null;
	
	@Field String MacroText = null;
	@Field ZType  MacroType = null;

	public BAsmNode(BNode ParentNode, String LibName, String MacroText, ZType MacroType) {
		super(ParentNode, null, 2);
		this.RequiredLibrary = LibName;
		this.MacroText = MacroText;
		this.MacroType = MacroType;
	}

	public final ZType MacroType() {
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

	@Override public void Accept(ZVisitor Visitor) {
		Visitor.VisitAsmNode(this);
	}

}
