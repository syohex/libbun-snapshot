package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZThrowNode;
import libbun.parser.sugar.ZAssertNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;


public class AssertPatternFunction extends ZMatchFunction {
	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode AssertNode = new ZAssertNode(ParentNode);
		AssertNode = TokenContext.MatchToken(AssertNode, "assert", ZTokenContext._Required);
		AssertNode = TokenContext.MatchToken(AssertNode, "(", ZTokenContext._Required);
		AssertNode = TokenContext.MatchPattern(AssertNode, ZThrowNode._Expr, "$Expression$", ZTokenContext._Required, ZTokenContext._AllowSkipIndent);
		AssertNode = TokenContext.MatchToken(AssertNode, ")", ZTokenContext._Required);
		return AssertNode;
	}

}
