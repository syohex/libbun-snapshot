package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZGroupNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class GroupPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode GroupNode = new ZGroupNode(ParentNode);
		GroupNode = TokenContext.MatchToken(GroupNode, "(", ZTokenContext._Required);
		GroupNode = TokenContext.MatchPattern(GroupNode, ZGroupNode._Expr, "$Expression$", ZTokenContext._Required, ZTokenContext._AllowSkipIndent);
		GroupNode = TokenContext.MatchToken(GroupNode, ")", ZTokenContext._Required);
		return GroupNode;
	}

}
