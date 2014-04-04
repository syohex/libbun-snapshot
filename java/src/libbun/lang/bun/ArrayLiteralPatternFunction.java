package libbun.lang.bun;

import libbun.ast.BArrayLiteralNode;
import libbun.ast.BNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ArrayLiteralPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LiteralNode = new BArrayLiteralNode(ParentNode);
		LiteralNode = TokenContext.MatchNtimes(LiteralNode, "[", "$Expression$", ",", "]");
		return LiteralNode;
	}

}
