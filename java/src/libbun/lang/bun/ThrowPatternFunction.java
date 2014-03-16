package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZThrowNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class ThrowPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode ThrowNode = new ZThrowNode(ParentNode);
		ThrowNode = TokenContext.MatchToken(ThrowNode, "throw", ZTokenContext._Required);
		ThrowNode = TokenContext.MatchPattern(ThrowNode, ZThrowNode._Expr, "$Expression$", ZTokenContext._Required);
		return ThrowNode;
	}

}
