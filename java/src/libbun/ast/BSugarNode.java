package libbun.ast;

import libbun.parser.BGenerator;
import libbun.parser.BToken;
import libbun.parser.BTypeChecker;
import libbun.parser.BVisitor;


public abstract class BSugarNode extends BNode {

	public BSugarNode(BNode ParentNode, BToken SourceToken, int Size) {
		super(ParentNode, SourceToken, Size);
	}

	@Override public final void Accept(BVisitor Visitor) {
		Visitor.VisitSugarNode(this);
	}

	public abstract BDesugarNode DeSugar(BGenerator Generator, BTypeChecker TypeChekcer);



}
