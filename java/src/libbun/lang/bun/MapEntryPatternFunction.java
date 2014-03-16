package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZMapEntryNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class MapEntryPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode LiteralNode = new ZMapEntryNode(ParentNode);
		LiteralNode = TokenContext.MatchPattern(LiteralNode, ZMapEntryNode._Key, "$Expression$", ZTokenContext._Required);
		LiteralNode = TokenContext.MatchToken(LiteralNode, ":", ZTokenContext._Required);
		LiteralNode = TokenContext.MatchPattern(LiteralNode, ZMapEntryNode._Value, "$Expression$", ZTokenContext._Required);
		return LiteralNode;
	}

}
