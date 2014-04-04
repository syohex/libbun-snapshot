package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BNullNode;
import libbun.util.BMatchFunction;

public class NullPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		return new BNullNode(ParentNode, TokenContext.GetToken(ZTokenContext._MoveNext));
	}

}
