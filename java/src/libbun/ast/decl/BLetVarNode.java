package libbun.ast.decl;

import libbun.ast.BListNode;
import libbun.ast.BNode;
import libbun.ast.literal.BConstNode;
import libbun.ast.literal.BDefaultValueNode;
import libbun.parser.BGenerator;
import libbun.parser.BVisitor;
import libbun.type.BType;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.Nullable;
import libbun.util.Var;

public class BLetVarNode extends BListNode {
	public static final int _NameInfo = 0;
	public static final int _TypeInfo = 1;
	public final static int _InitValue = 2;
	// this is used for multiple declaration of variables
	public final static int _NextVar = 3;  // by apppend

	public final static int _IsExport   = 1;
	public final static int _IsReadOnly = 1 << 1;
	public final static int _IsDefined  = 1 << 2;
	public final static int _IsUsed     = 1 << 3;

	@BField public int NameFlag = 0;
	@BField public BType   GivenType = null;
	@BField public String  GivenName = null;
	@BField public int NameIndex = 0;

	public BLetVarNode(BNode ParentNode, int NameFlag, @Nullable BType GivenType, @Nullable String GivenName) {
		super(ParentNode, null, 3);
		this.NameFlag = NameFlag;
		this.GivenType = GivenType;
		this.GivenName = GivenName;
	}

	public final boolean IsExport() {  // export let at top level
		return BLib._IsFlag(this.NameFlag, BLetVarNode._IsExport);
	}

	public final boolean IsReadOnly() {   // let readonly var writable
		return BLib._IsFlag(this.NameFlag, BLetVarNode._IsReadOnly);
	}

	public final boolean IsDefined() {    // if assigned
		return BLib._IsFlag(this.NameFlag, BLetVarNode._IsDefined);
	}

	public final boolean IsUsed() {
		return BLib._IsFlag(this.NameFlag, BLetVarNode._IsUsed);
	}

	public final void Defined() {
		this.NameFlag = this.NameFlag | BLetVarNode._IsDefined;
	}

	public final void Used() {
		this.NameFlag = this.NameFlag | BLetVarNode._IsUsed;
	}

	public final BType DeclType() {
		if(this.GivenType == null) {
			if(this.AST[BLetVarNode._TypeInfo] != null) {
				this.GivenType = this.AST[BLetVarNode._TypeInfo].Type;
			}
			else {
				this.GivenType = BType.VarType;
			}
		}
		return this.GivenType;
	}

	public final void SetDeclType(BType Type) {
		this.GivenType = Type;
	}

	public final String GetGivenName() {
		if(this.GivenName == null) {
			this.GivenName = this.AST[BLetVarNode._NameInfo].SourceToken.GetTextAsName();
		}
		return this.GivenName;
	}

	public final String GetUniqueName(BGenerator Generator) {
		if(this.NameIndex == 0 || this.IsExport()) {
			return this.GetGivenName();
		}
		return Generator.NameUniqueSymbol(this.GetGivenName(), this.NameIndex);
	}


	public final BNode InitValueNode() {
		if(this.AST[BLetVarNode._InitValue] == null) {
			this.SetNode(BLetVarNode._InitValue, new BDefaultValueNode());
		}
		return this.AST[BLetVarNode._InitValue];
	}

	@Override public final void Accept(BVisitor Visitor) {
		Visitor.VisitLetNode(this);
	}

	public final boolean IsParamNode() {
		return this.ParentNode instanceof BFunctionNode;
	}

	public final boolean IsConstValue() {
		return this.InitValueNode() instanceof BConstNode;
	}


	public final boolean HasNextVarNode() {
		return BLetVarNode._NextVar < this.GetAstSize();
	}

	public final BLetVarNode NextVarNode() {
		if(BLetVarNode._NextVar < this.GetAstSize()) {
			@Var BNode VarNode = this.AST[BLetVarNode._NextVar];
			if(VarNode instanceof BLetVarNode) {
				return (BLetVarNode)VarNode;
			}
		}
		return null;
	}

	public final boolean AppendVarNode(BLetVarNode VarNode) {
		if(this.HasNextVarNode()) {
			return this.NextVarNode().AppendVarNode(VarNode);
		}
		this.Append(VarNode, BNode._EnforcedParent);
		return true;
	}

}
