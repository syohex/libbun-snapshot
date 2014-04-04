package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BNullNode;
import libbun.util.BMatchFunction;

public class NullPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		return new BNullNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext));
	}

}
