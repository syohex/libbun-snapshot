package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZBreakNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class BreakPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode BreakNode = new ZBreakNode(ParentNode);
		BreakNode = TokenContext.MatchToken(BreakNode, "break", ZTokenContext._Required);
		return BreakNode;
	}

}
