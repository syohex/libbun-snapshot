package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.expression.BMethodCallNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class MethodCallPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode MethodCallNode = new BMethodCallNode(ParentNode, LeftNode);
		MethodCallNode = TokenContext.MatchToken(MethodCallNode, ".", BTokenContext._Required);
		MethodCallNode = TokenContext.MatchPattern(MethodCallNode, BMethodCallNode._NameInfo, "$Name$", BTokenContext._Required);
		MethodCallNode = TokenContext.MatchNtimes(MethodCallNode, "(", "$Expression$", ",", ")");
		return MethodCallNode;
	}

}
