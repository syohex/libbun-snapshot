package libbun.parser.ast;

import libbun.parser.BToken;
import libbun.parser.BVisitor;


public abstract class ZLocalDefinedNode extends BNode {


	public ZLocalDefinedNode(BNode ParentNode, BToken SourceToken, int Size) {
		super(ParentNode, SourceToken, Size);
	}

	@Override public final void Accept(BVisitor Visitor) {
		Visitor.VisitLocalDefinedNode(this);
	}

	//	@Field public ZType   GivenType = null;
	//	@Field public String  GivenName = null;
	//	@Field public ZToken  GivenNameToken = null;
	//	@Field public ZToken  GivenTypeToken = null;
	//	@Override public void SetTypeInfo(ZToken TypeToken, ZType Type) {
	//		this.GivenTypeToken = TypeToken;
	//		this.GivenType = Type;
	//	}
	//
	//	@Override public void SetNameInfo(ZToken NameToken, String Name) {
	//		this.GivenName = Name;
	//		this.GivenNameToken = NameToken;
	//	}

}
