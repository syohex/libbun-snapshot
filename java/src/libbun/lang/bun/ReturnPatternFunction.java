package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZReturnNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ReturnPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ReturnNode = new ZReturnNode(ParentNode);
		ReturnNode = TokenContext.MatchToken(ReturnNode, "return", ZTokenContext._Required);
		ReturnNode = TokenContext.MatchPattern(ReturnNode, ZReturnNode._Expr, "$Expression$", ZTokenContext._Optional);
		return ReturnNode;
	}

}
