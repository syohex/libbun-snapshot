package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNode;
import libbun.util.ZMatchFunction;

public class InStatementPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		TokenContext.SetParseFlag(ZTokenContext._AllowSkipIndent);
		return ExpressionPatternFunction._DispatchPattern(ParentNode, TokenContext, null, true, true);
	}

}
