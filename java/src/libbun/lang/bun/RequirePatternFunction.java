package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZRequireNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class RequirePatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode RequireNode = new ZRequireNode(ParentNode);
		RequireNode = TokenContext.MatchToken(RequireNode, "require", ZTokenContext._Required);
		RequireNode = TokenContext.MatchPattern(RequireNode, ZRequireNode._Path, "$StringLiteral$", ZTokenContext._Required);
		return RequireNode;
	}

}
