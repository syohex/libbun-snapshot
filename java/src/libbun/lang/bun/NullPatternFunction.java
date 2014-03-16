package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZNullNode;
import libbun.util.ZMatchFunction;

public class NullPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		return new ZNullNode(ParentNode, TokenContext.GetToken(ZTokenContext._MoveNext));
	}

}
