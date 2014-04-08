package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.literal.BunMapEntryNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class MapEntryPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LiteralNode = new BunMapEntryNode(ParentNode);
		LiteralNode = TokenContext.MatchPattern(LiteralNode, BunMapEntryNode._Key, "$Expression$", BTokenContext._Required);
		LiteralNode = TokenContext.MatchToken(LiteralNode, ":", BTokenContext._Required);
		LiteralNode = TokenContext.MatchPattern(LiteralNode, BunMapEntryNode._Value, "$Expression$", BTokenContext._Required);
		return LiteralNode;
	}

}
