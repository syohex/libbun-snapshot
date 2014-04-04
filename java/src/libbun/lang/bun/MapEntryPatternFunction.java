package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZMapEntryNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class MapEntryPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LiteralNode = new ZMapEntryNode(ParentNode);
		LiteralNode = TokenContext.MatchPattern(LiteralNode, ZMapEntryNode._Key, "$Expression$", ZTokenContext._Required);
		LiteralNode = TokenContext.MatchToken(LiteralNode, ":", ZTokenContext._Required);
		LiteralNode = TokenContext.MatchPattern(LiteralNode, ZMapEntryNode._Value, "$Expression$", ZTokenContext._Required);
		return LiteralNode;
	}

}
