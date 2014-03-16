package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNode;
import libbun.util.ZMatchFunction;

public class RightExpressionPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		return ExpressionPatternFunction._DispatchPattern(ParentNode, TokenContext, LeftNode, false, false);
	}

}
