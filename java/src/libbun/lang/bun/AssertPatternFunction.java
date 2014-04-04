package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZThrowNode;
import libbun.parser.sugar.ZAssertNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;


public class AssertPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode AssertNode = new ZAssertNode(ParentNode);
		AssertNode = TokenContext.MatchToken(AssertNode, "assert", BTokenContext._Required);
		AssertNode = TokenContext.MatchToken(AssertNode, "(", BTokenContext._Required);
		AssertNode = TokenContext.MatchPattern(AssertNode, ZThrowNode._Expr, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		AssertNode = TokenContext.MatchToken(AssertNode, ")", BTokenContext._Required);
		return AssertNode;
	}

}
