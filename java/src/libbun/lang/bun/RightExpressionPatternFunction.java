package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;

public class RightExpressionPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		return ExpressionPatternFunction._DispatchPattern(ParentNode, TokenContext, LeftNode, false, false);
	}

}
