package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZGroupNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class GroupPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode GroupNode = new ZGroupNode(ParentNode);
		GroupNode = TokenContext.MatchToken(GroupNode, "(", BTokenContext._Required);
		GroupNode = TokenContext.MatchPattern(GroupNode, ZGroupNode._Expr, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		GroupNode = TokenContext.MatchToken(GroupNode, ")", BTokenContext._Required);
		return GroupNode;
	}

}
