package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZReturnNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class ReturnPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode ReturnNode = new ZReturnNode(ParentNode);
		ReturnNode = TokenContext.MatchToken(ReturnNode, "return", ZTokenContext._Required);
		ReturnNode = TokenContext.MatchPattern(ReturnNode, ZReturnNode._Expr, "$Expression$", ZTokenContext._Optional);
		return ReturnNode;
	}

}
