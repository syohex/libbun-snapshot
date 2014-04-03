package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZMapLiteralNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class MapLiteralPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LiteralNode = new ZMapLiteralNode(ParentNode);
		LiteralNode = TokenContext.MatchNtimes(LiteralNode, "{", "$MapEntry$", ",", "}");
		return LiteralNode;
	}

}
