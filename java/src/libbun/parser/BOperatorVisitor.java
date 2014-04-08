package libbun.parser;

import libbun.ast.binary.BunAddNode;
import libbun.ast.binary.BunBitwiseAndNode;
import libbun.ast.binary.BunBitwiseOrNode;
import libbun.ast.binary.BunBitwiseXorNode;
import libbun.ast.binary.BunDivNode;
import libbun.ast.binary.BunEqualsNode;
import libbun.ast.binary.BunGreaterThanEqualsNode;
import libbun.ast.binary.BunGreaterThanNode;
import libbun.ast.binary.BunLeftShiftNode;
import libbun.ast.binary.BunLessThanEqualsNode;
import libbun.ast.binary.BunLessThanNode;
import libbun.ast.binary.BunModNode;
import libbun.ast.binary.BunMulNode;
import libbun.ast.binary.BunNotEqualsNode;
import libbun.ast.binary.BunRightShiftNode;
import libbun.ast.binary.BunSubNode;
import libbun.ast.unary.BunComplementNode;
import libbun.ast.unary.BunMinusNode;
import libbun.ast.unary.BunPlusNode;

public abstract class BOperatorVisitor extends BVisitor {

	// Unary
	public abstract void VisitPlusNode(BunPlusNode Node);
	public abstract void VisitMinusNode(BunMinusNode Node);
	public abstract void VisitComplementNode(BunComplementNode Node);

	// BinaryNode
	public abstract void VisitAddNode(BunAddNode Node);
	public abstract void VisitSubNode(BunSubNode Node);
	public abstract void VisitMulNode(BunMulNode Node);
	public abstract void VisitDivNode(BunDivNode Node);
	public abstract void VisitModNode(BunModNode Node);

	public abstract void VisitLeftShiftNode(BunLeftShiftNode Node);
	public abstract void VisitRightShiftNode(BunRightShiftNode Node);
	public abstract void VisitBitwiseAndNode(BunBitwiseAndNode Node);
	public abstract void VisitBitwiseOrNode(BunBitwiseOrNode Node);
	public abstract void VisitBitwiseXorNode(BunBitwiseXorNode Node);

	// Comparator
	public abstract void VisitEqualsNode(BunEqualsNode Node);
	public abstract void VisitNotEqualsNode(BunNotEqualsNode Node);
	public abstract void VisitLessThanNode(BunLessThanNode Node);
	public abstract void VisitLessThanEqualsNode(BunLessThanEqualsNode Node);
	public abstract void VisitGreaterThanNode(BunGreaterThanNode Node);
	public abstract void VisitGreaterThanEqualsNode(BunGreaterThanEqualsNode Node);

}
