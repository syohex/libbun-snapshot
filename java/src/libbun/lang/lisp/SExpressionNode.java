package libbun.lang.lisp;

import libbun.ast.BNode;
import libbun.ast.DesugarNode;
import libbun.ast.SyntaxSugarNode;
import libbun.encode.LibBunGenerator;
import libbun.parser.LibBunTypeChecker;

public class SExpressionNode extends SyntaxSugarNode {

	public SExpressionNode(BNode ParentNode) {
		super(ParentNode, 0);
	}

	//	private BNode CreateBunNode() {
	//		@Var String Symbol = this.GetSymbol();
	//		if(this.GetAstSize() == 0) {
	//			return new BunNullNode(null, null);
	//		}
	//		else if(this.GetAstSize() == 1) {
	//			return new FuncCallNode(null, this.AST[1]);
	//		}
	//		else if(this.GetAstSize() == 2) {
	//
	//		}
	//	}

	@Override public DesugarNode DeSugar(LibBunGenerator Generator, LibBunTypeChecker TypeChekcer) {
		//		@Var DesugarNode Node = new DesugarNode(this, null);
		// TODO Auto-generated method stub
		return null;
	}

}
