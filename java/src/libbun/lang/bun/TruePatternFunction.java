package libbun.lang.bun;

import libbun.ast.BBooleanNode;
import libbun.ast.BNode;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;

public class TruePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		return new BBooleanNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), true);
	}

}
