package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZThrowNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ThrowPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ThrowNode = new ZThrowNode(ParentNode);
		ThrowNode = TokenContext.MatchToken(ThrowNode, "throw", BTokenContext._Required);
		ThrowNode = TokenContext.MatchPattern(ThrowNode, ZThrowNode._Expr, "$Expression$", BTokenContext._Required);
		return ThrowNode;
	}

}
