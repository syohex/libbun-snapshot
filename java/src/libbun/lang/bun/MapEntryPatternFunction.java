package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZMapEntryNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class MapEntryPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LiteralNode = new ZMapEntryNode(ParentNode);
		LiteralNode = TokenContext.MatchPattern(LiteralNode, ZMapEntryNode._Key, "$Expression$", BTokenContext._Required);
		LiteralNode = TokenContext.MatchToken(LiteralNode, ":", BTokenContext._Required);
		LiteralNode = TokenContext.MatchPattern(LiteralNode, ZMapEntryNode._Value, "$Expression$", BTokenContext._Required);
		return LiteralNode;
	}

}
