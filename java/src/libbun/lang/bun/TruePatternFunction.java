package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BBooleanNode;
import libbun.parser.ast.BNode;
import libbun.util.BMatchFunction;

public class TruePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		return new BBooleanNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), true);
	}

}
