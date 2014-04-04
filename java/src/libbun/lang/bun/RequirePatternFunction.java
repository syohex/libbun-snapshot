package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZRequireNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class RequirePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode RequireNode = new ZRequireNode(ParentNode);
		RequireNode = TokenContext.MatchToken(RequireNode, "require", BTokenContext._Required);
		RequireNode = TokenContext.MatchPattern(RequireNode, ZRequireNode._Path, "$StringLiteral$", BTokenContext._Required);
		return RequireNode;
	}

}
