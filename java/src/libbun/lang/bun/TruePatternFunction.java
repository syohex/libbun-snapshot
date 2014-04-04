package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BBooleanNode;
import libbun.parser.ast.BNode;
import libbun.util.BMatchFunction;

public class TruePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		return new BBooleanNode(ParentNode, TokenContext.GetToken(ZTokenContext._MoveNext), true);
	}

}
