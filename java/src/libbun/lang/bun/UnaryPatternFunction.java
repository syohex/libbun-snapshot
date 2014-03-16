package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZUnaryNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class UnaryPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode UnaryNode = new ZUnaryNode(ParentNode, TokenContext.GetToken(ZTokenContext._MoveNext));
		return TokenContext.MatchPattern(UnaryNode, ZUnaryNode._Recv, "$RightExpression$", ZTokenContext._Required);
	}

}
