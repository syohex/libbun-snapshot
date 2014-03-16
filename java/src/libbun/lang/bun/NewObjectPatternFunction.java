package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNewObjectNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class NewObjectPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode LiteralNode = new ZNewObjectNode(ParentNode);
		LiteralNode = TokenContext.MatchToken(LiteralNode, "new", ZTokenContext._Required);
		LiteralNode = TokenContext.MatchPattern(LiteralNode, ZNewObjectNode._TypeInfo, "$OpenType$", ZTokenContext._Optional);
		LiteralNode = TokenContext.MatchNtimes(LiteralNode, "(", "$Expression$", ",", ")");
		return LiteralNode;
	}

}
