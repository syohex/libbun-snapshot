package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZArrayLiteralNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class ArrayLiteralPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LiteralNode = new ZArrayLiteralNode(ParentNode);
		LiteralNode = TokenContext.MatchNtimes(LiteralNode, "[", "$Expression$", ",", "]");
		return LiteralNode;
	}

}
