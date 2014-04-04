package libbun.lang.bun;

import libbun.ast.BGroupNode;
import libbun.ast.BNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class GroupPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode GroupNode = new BGroupNode(ParentNode);
		GroupNode = TokenContext.MatchToken(GroupNode, "(", BTokenContext._Required);
		GroupNode = TokenContext.MatchPattern(GroupNode, BGroupNode._Expr, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		GroupNode = TokenContext.MatchToken(GroupNode, ")", BTokenContext._Required);
		return GroupNode;
	}

}
