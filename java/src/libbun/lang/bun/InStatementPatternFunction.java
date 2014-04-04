package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.util.BMatchFunction;

public class InStatementPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		TokenContext.SetParseFlag(BTokenContext._AllowSkipIndent);
		return ExpressionPatternFunction._DispatchPattern(ParentNode, TokenContext, null, true, true);
	}

}
