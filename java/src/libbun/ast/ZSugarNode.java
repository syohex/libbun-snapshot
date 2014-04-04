package libbun.ast;

import libbun.parser.BGenerator;
import libbun.parser.BToken;
import libbun.parser.BTypeChecker;
import libbun.parser.BVisitor;


public abstract class ZSugarNode extends BNode {

	public ZSugarNode(BNode ParentNode, BToken SourceToken, int Size) {
		super(ParentNode, SourceToken, Size);
	}

	@Override public final void Accept(BVisitor Visitor) {
		Visitor.VisitSugarNode(this);
	}

	public abstract ZDesugarNode DeSugar(BGenerator Generator, BTypeChecker TypeChekcer);



}
