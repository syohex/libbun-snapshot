package libbun.parser.ast;

import libbun.type.ZType;
import libbun.util.Field;

public abstract class ZDefineNameNode extends BNode {
	public static final int _NameInfo = 0;
	public static final int _TypeInfo = 1;
	public final static int _InitValue = 2;

	@Field public boolean IsReadOnly = false;
	@Field public ZType   GivenType = null;
	@Field public String  GivenName = null;

	protected ZDefineNameNode(BNode ParentNode, int Size) {
		super(ParentNode, null, Size);
	}

	public final ZType DeclType() {
		if(this.GivenType == null) {
			if(this.AST[BLetVarNode._TypeInfo] != null) {
				this.GivenType = this.AST[BLetVarNode._TypeInfo].Type;
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
			this.GivenName = this.AST[BLetVarNode._NameInfo].SourceToken.GetTextAsName();
		}
		return this.GivenName;
	}

	public final BNode InitValueNode() {
		if(this.AST[BLetVarNode._InitValue] == null) {
			this.SetNode(BLetVarNode._InitValue, new ZDefaultValueNode());
		}
		return this.AST[BLetVarNode._InitValue];
	}

}
