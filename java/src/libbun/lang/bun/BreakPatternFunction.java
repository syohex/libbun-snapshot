package libbun.lang.bun;

import libbun.ast.BBreakNode;
import libbun.ast.BNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class BreakPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode BreakNode = new BBreakNode(ParentNode);
		BreakNode = TokenContext.MatchToken(BreakNode, "break", BTokenContext._Required);
		return BreakNode;
	}

}
