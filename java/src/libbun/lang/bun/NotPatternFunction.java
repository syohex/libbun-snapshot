package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZNotNode;
import libbun.parser.ast.ZUnaryNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class NotPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode UnaryNode = new ZNotNode(ParentNode, TokenContext.GetToken(ZTokenContext._MoveNext));
		UnaryNode = TokenContext.MatchPattern(UnaryNode, ZUnaryNode._Recv, "$RightExpression$", ZTokenContext._Required);
		return UnaryNode;
	}

}
