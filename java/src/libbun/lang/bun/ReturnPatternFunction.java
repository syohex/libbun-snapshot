package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZReturnNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ReturnPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ReturnNode = new ZReturnNode(ParentNode);
		ReturnNode = TokenContext.MatchToken(ReturnNode, "return", BTokenContext._Required);
		ReturnNode = TokenContext.MatchPattern(ReturnNode, ZReturnNode._Expr, "$Expression$", BTokenContext._Optional);
		return ReturnNode;
	}

}
