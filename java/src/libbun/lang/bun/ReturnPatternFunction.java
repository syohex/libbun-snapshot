package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.statement.BunReturnNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ReturnPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ReturnNode = new BunReturnNode(ParentNode);
		ReturnNode = TokenContext.MatchToken(ReturnNode, "return", BTokenContext._Required);
		ReturnNode = TokenContext.MatchPattern(ReturnNode, BunReturnNode._Expr, "$Expression$", BTokenContext._Optional);
		return ReturnNode;
	}

}
