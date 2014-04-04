package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZBreakNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class BreakPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode BreakNode = new ZBreakNode(ParentNode);
		BreakNode = TokenContext.MatchToken(BreakNode, "break", BTokenContext._Required);
		return BreakNode;
	}

}
