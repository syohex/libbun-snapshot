package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZThrowNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class ThrowPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ThrowNode = new ZThrowNode(ParentNode);
		ThrowNode = TokenContext.MatchToken(ThrowNode, "throw", ZTokenContext._Required);
		ThrowNode = TokenContext.MatchPattern(ThrowNode, ZThrowNode._Expr, "$Expression$", ZTokenContext._Required);
		return ThrowNode;
	}

}
