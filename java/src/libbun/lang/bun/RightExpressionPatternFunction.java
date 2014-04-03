package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.util.ZMatchFunction;

public class RightExpressionPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		return ExpressionPatternFunction._DispatchPattern(ParentNode, TokenContext, LeftNode, false, false);
	}

}
