package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.util.BMatchFunction;

public class InStatementPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		TokenContext.SetParseFlag(ZTokenContext._AllowSkipIndent);
		return ExpressionPatternFunction._DispatchPattern(ParentNode, TokenContext, null, true, true);
	}

}
