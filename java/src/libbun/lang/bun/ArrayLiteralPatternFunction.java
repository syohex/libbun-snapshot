package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZArrayLiteralNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class ArrayLiteralPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode LiteralNode = new ZArrayLiteralNode(ParentNode);
		LiteralNode = TokenContext.MatchNtimes(LiteralNode, "[", "$Expression$", ",", "]");
		return LiteralNode;
	}

}
