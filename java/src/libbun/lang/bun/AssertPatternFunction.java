package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZThrowNode;
import libbun.parser.sugar.ZAssertNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;


public class AssertPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode AssertNode = new ZAssertNode(ParentNode);
		AssertNode = TokenContext.MatchToken(AssertNode, "assert", ZTokenContext._Required);
		AssertNode = TokenContext.MatchToken(AssertNode, "(", ZTokenContext._Required);
		AssertNode = TokenContext.MatchPattern(AssertNode, ZThrowNode._Expr, "$Expression$", ZTokenContext._Required, ZTokenContext._AllowSkipIndent);
		AssertNode = TokenContext.MatchToken(AssertNode, ")", ZTokenContext._Required);
		return AssertNode;
	}

}
