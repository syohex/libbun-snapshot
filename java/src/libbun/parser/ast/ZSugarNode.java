package libbun.parser.ast;

import libbun.parser.ZGenerator;
import libbun.parser.ZToken;
import libbun.parser.ZTypeChecker;
import libbun.parser.ZVisitor;


public abstract class ZSugarNode extends ZNode {

	public ZSugarNode(ZNode ParentNode, ZToken SourceToken, int Size) {
		super(ParentNode, SourceToken, Size);
	}

	@Override public final void Accept(ZVisitor Visitor) {
		Visitor.VisitSugarNode(this);
	}

	public abstract ZDesugarNode DeSugar(ZGenerator Generator, ZTypeChecker TypeChekcer);



}
