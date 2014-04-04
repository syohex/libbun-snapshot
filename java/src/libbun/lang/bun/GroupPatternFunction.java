package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZGroupNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class GroupPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode GroupNode = new ZGroupNode(ParentNode);
		GroupNode = TokenContext.MatchToken(GroupNode, "(", ZTokenContext._Required);
		GroupNode = TokenContext.MatchPattern(GroupNode, ZGroupNode._Expr, "$Expression$", ZTokenContext._Required, ZTokenContext._AllowSkipIndent);
		GroupNode = TokenContext.MatchToken(GroupNode, ")", ZTokenContext._Required);
		return GroupNode;
	}

}
