package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;

public class TypeAnnotationPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		if(TokenContext.MatchToken(":")) {
			return TokenContext.ParsePattern(ParentNode, "$OpenType$", BTokenContext._Required);
		}
		return null;
	}

}
