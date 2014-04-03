package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.util.ZMatchFunction;

public class TypeAnnotationPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		if(TokenContext.MatchToken(":")) {
			return TokenContext.ParsePattern(ParentNode, "$OpenType$", ZTokenContext._Required);
		}
		return null;
	}

}
