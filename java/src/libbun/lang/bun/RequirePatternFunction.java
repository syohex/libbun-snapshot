package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.BRequireNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class RequirePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode RequireNode = new BRequireNode(ParentNode);
		RequireNode = TokenContext.MatchToken(RequireNode, "require", BTokenContext._Required);
		RequireNode = TokenContext.MatchPattern(RequireNode, BRequireNode._Path, "$StringLiteral$", BTokenContext._Required);
		return RequireNode;
	}

}
