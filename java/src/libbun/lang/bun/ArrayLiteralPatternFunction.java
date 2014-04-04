package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZArrayLiteralNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ArrayLiteralPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LiteralNode = new ZArrayLiteralNode(ParentNode);
		LiteralNode = TokenContext.MatchNtimes(LiteralNode, "[", "$Expression$", ",", "]");
		return LiteralNode;
	}

}
