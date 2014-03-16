package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZMapLiteralNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class MapLiteralPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode LiteralNode = new ZMapLiteralNode(ParentNode);
		LiteralNode = TokenContext.MatchNtimes(LiteralNode, "{", "$MapEntry$", ",", "}");
		return LiteralNode;
	}

}
