package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.GroupNode;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;
import libbun.util.Var;

public class GroupPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode Node = new GroupNode(ParentNode);
		Node = TokenContext.MatchToken(Node, "(", BTokenContext._Required);
		Node = TokenContext.MatchPattern(Node, GroupNode._Expr, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		Node = TokenContext.MatchToken(Node, ")", BTokenContext._Required);
		return Node;
	}
}
