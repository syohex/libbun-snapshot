package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.literal.BNullNode;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;

public class NullPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		return new BNullNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext));
	}

}
