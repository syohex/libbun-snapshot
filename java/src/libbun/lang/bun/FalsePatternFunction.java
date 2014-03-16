package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZBooleanNode;
import libbun.parser.ast.ZNode;
import libbun.util.ZMatchFunction;

public class FalsePatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		return new ZBooleanNode(ParentNode, TokenContext.GetToken(ZTokenContext._MoveNext), false);
	}

}
