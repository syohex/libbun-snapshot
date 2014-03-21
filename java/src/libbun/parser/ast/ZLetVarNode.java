package libbun.parser.ast;

import libbun.parser.ZVisitor;
import libbun.type.ZType;
import libbun.util.Field;
import libbun.util.LibZen;
import libbun.util.Var;

public class ZLetVarNode extends ZListNode {
	public static final int _NameInfo = 0;
	public static final int _TypeInfo = 1;
	public final static int _InitValue = 2;
	// this is used for multiple declaration of variables
	public final static int _NextVar = 3;

	public final static int _IsExport   = 1;
	public final static int _IsReadOnly = 1 << 1;
	public final static int _IsDefined  = 1 << 2;
	public final static int _IsUsed     = 1 << 3;

	@Field public int NameFlag = 0;

	@Field public ZType   GivenType = null;
	@Field public String  GivenName = null;
	@Field public String  GlobalName = null;

	public ZLetVarNode(ZNode ParentNode, int NameFlag) {
		super(ParentNode, null, 3);
		this.NameFlag = NameFlag;
	}

	public final boolean IsExport() {
		return LibZen._IsFlag(this.NameFlag, ZLetVarNode._IsExport);
	}

	public final boolean IsReadOnly() {
		return LibZen._IsFlag(this.NameFlag, ZLetVarNode._IsReadOnly);
	}

	public final boolean IsDefined() {
		return LibZen._IsFlag(this.NameFlag, ZLetVarNode._IsDefined);
	}

	public final boolean IsUsed() {
		return LibZen._IsFlag(this.NameFlag, ZLetVarNode._IsUsed);
	}

	public final void Defined() {
		this.NameFlag = this.NameFlag | ZLetVarNode._IsDefined;
	}

	public final void Used() {
		this.NameFlag = this.NameFlag | ZLetVarNode._IsUsed;
	}


	public final ZType DeclType() {
		if(this.GivenType == null) {
			if(this.AST[ZLetVarNode._TypeInfo] != null) {
				this.GivenType = this.AST[ZLetVarNode._TypeInfo].Type;
			}
			else {
				this.GivenType = ZType.VarType;
			}
		}
		return this.GivenType;
	}

	public final void SetDeclType(ZType Type) {
		this.GivenType = Type;
	}

	public final String GetName() {
		if(this.GivenName == null) {
			this.GivenName = this.AST[ZLetVarNode._NameInfo].SourceToken.GetTextAsName();
		}
		return this.GivenName;
	}

	public final ZNode InitValueNode() {
		if(this.AST[ZLetVarNode._InitValue] == null) {
			this.SetNode(ZLetVarNode._InitValue, new ZDefaultValueNode());
		}
		return this.AST[ZLetVarNode._InitValue];
	}

	@Override public final void Accept(ZVisitor Visitor) {
		Visitor.VisitLetNode(this);
	}

	public final boolean IsParamNode() {
		return this.ParentNode instanceof ZFunctionNode;
	}

	public final boolean IsConstValue() {
		return this.InitValueNode() instanceof ZConstNode;
	}


	public final boolean HasNextVarNode() {
		return ZLetVarNode._NextVar < this.GetAstSize();
	}

	public final ZLetVarNode NextVarNode() {
		if(ZLetVarNode._NextVar < this.GetAstSize()) {
			@Var ZNode VarNode = this.AST[ZLetVarNode._NextVar];
			if(VarNode instanceof ZLetVarNode) {
				return (ZLetVarNode)VarNode;
			}
		}
		return null;
	}

	public final boolean AppendVarNode(ZLetVarNode VarNode) {
		if(this.HasNextVarNode()) {
			return this.NextVarNode().AppendVarNode(VarNode);
		}
		this.Append(VarNode, ZNode._EnforcedParent);
		return true;
	}


}
