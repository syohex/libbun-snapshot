package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZBreakNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class BreakPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode BreakNode = new ZBreakNode(ParentNode);
		BreakNode = TokenContext.MatchToken(BreakNode, "break", ZTokenContext._Required);
		return BreakNode;
	}

}
