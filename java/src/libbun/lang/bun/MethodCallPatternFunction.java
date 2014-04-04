package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZMethodCallNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class MethodCallPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode MethodCallNode = new ZMethodCallNode(ParentNode, LeftNode);
		MethodCallNode = TokenContext.MatchToken(MethodCallNode, ".", BTokenContext._Required);
		MethodCallNode = TokenContext.MatchPattern(MethodCallNode, ZMethodCallNode._NameInfo, "$Name$", BTokenContext._Required);
		MethodCallNode = TokenContext.MatchNtimes(MethodCallNode, "(", "$Expression$", ",", ")");
		return MethodCallNode;
	}

}
