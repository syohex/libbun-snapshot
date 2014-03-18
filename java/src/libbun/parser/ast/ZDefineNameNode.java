package libbun.parser.ast;

import libbun.type.ZType;
import libbun.util.Field;

public abstract class ZDefineNameNode extends ZNode {
	public static final int _NameInfo = 0;
	public static final int _TypeInfo = 1;
	public final static int _InitValue = 2;

	@Field public boolean IsReadOnly = false;
	@Field public ZType   GivenType = null;
	@Field public String  GivenName = null;

	protected ZDefineNameNode(ZNode ParentNode, int Size) {
		super(ParentNode, null, Size);
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

}
