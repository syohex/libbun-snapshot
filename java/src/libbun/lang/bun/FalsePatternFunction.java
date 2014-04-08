package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.literal.BunBooleanNode;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;

public class FalsePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		return new BunBooleanNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), false);
	}

}
