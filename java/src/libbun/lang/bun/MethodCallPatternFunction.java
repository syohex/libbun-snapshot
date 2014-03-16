package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZMethodCallNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class MethodCallPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode MethodCallNode = new ZMethodCallNode(ParentNode, LeftNode);
		MethodCallNode = TokenContext.MatchToken(MethodCallNode, ".", ZTokenContext._Required);
		MethodCallNode = TokenContext.MatchPattern(MethodCallNode, ZMethodCallNode._NameInfo, "$Name$", ZTokenContext._Required);
		MethodCallNode = TokenContext.MatchNtimes(MethodCallNode, "(", "$Expression$", ",", ")");
		return MethodCallNode;
	}

}
