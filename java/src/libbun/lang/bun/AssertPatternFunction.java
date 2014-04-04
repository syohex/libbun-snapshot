package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.statement.BThrowNode;
import libbun.ast.sugar.ZAssertNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;


public class AssertPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode AssertNode = new ZAssertNode(ParentNode);
		AssertNode = TokenContext.MatchToken(AssertNode, "assert", BTokenContext._Required);
		AssertNode = TokenContext.MatchToken(AssertNode, "(", BTokenContext._Required);
		AssertNode = TokenContext.MatchPattern(AssertNode, BThrowNode._Expr, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		AssertNode = TokenContext.MatchToken(AssertNode, ")", BTokenContext._Required);
		return AssertNode;
	}

}
