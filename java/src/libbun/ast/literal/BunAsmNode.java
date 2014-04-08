package libbun.ast.literal;

import libbun.ast.BNode;
import libbun.parser.BVisitor;
import libbun.type.BType;
import libbun.util.BField;
import libbun.util.Var;

public class BunAsmNode extends BNode {
	public final static int _Macro = 0;
	public static final int _TypeInfo = 1;
	@BField public String RequiredLibrary = null;
	
	@BField String MacroText = null;
	@BField BType  MacroType = null;

	public BunAsmNode(BNode ParentNode, String LibName, String MacroText, BType MacroType) {
		super(ParentNode, null, 2);
		this.RequiredLibrary = LibName;
		this.MacroText = MacroText;
		this.MacroType = MacroType;
	}

	public final BType MacroType() {
		if(this.MacroType == null) {
			this.MacroType = this.AST[BunAsmNode._TypeInfo].Type;
		}
		return this.MacroType;
	}

	public final String GetMacroText() {
		if(this.MacroText == null) {
			@Var BNode Node = this.AST[BunAsmNode._Macro];
			if(Node instanceof BunStringNode) {
				this.MacroText = ((BunStringNode)Node).StringValue;
			}
		}
		return this.MacroText;
	}

	@Override public void Accept(BVisitor Visitor) {
		Visitor.VisitAsmNode(this);
	}

}
