package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.BThrowNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ThrowPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ThrowNode = new BThrowNode(ParentNode);
		ThrowNode = TokenContext.MatchToken(ThrowNode, "throw", BTokenContext._Required);
		ThrowNode = TokenContext.MatchPattern(ThrowNode, BThrowNode._Expr, "$Expression$", BTokenContext._Required);
		return ThrowNode;
	}

}
