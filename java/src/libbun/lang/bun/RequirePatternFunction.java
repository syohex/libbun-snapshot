package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZRequireNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class RequirePatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode RequireNode = new ZRequireNode(ParentNode);
		RequireNode = TokenContext.MatchToken(RequireNode, "require", ZTokenContext._Required);
		RequireNode = TokenContext.MatchPattern(RequireNode, ZRequireNode._Path, "$StringLiteral$", ZTokenContext._Required);
		return RequireNode;
	}

}
