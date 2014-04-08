package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.expression.MethodCallNode;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;
import libbun.util.Var;

public class MethodCallPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode Node = new MethodCallNode(ParentNode, LeftNode);
		Node = TokenContext.MatchToken(Node, ".", BTokenContext._Required);
		Node = TokenContext.MatchPattern(Node, MethodCallNode._NameInfo, "$Name$", BTokenContext._Required);
		Node = TokenContext.MatchNtimes(Node, "(", "$Expression$", ",", ")");
		return Node;
	}
}
