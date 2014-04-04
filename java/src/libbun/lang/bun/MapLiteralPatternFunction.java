package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZMapLiteralNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class MapLiteralPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LiteralNode = new ZMapLiteralNode(ParentNode);
		LiteralNode = TokenContext.MatchNtimes(LiteralNode, "{", "$MapEntry$", ",", "}");
		return LiteralNode;
	}

}
