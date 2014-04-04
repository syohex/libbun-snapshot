package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZNotNode;
import libbun.parser.ast.ZUnaryNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class NotPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode UnaryNode = new ZNotNode(ParentNode, TokenContext.GetToken(ZTokenContext._MoveNext));
		UnaryNode = TokenContext.MatchPattern(UnaryNode, ZUnaryNode._Recv, "$RightExpression$", ZTokenContext._Required);
		return UnaryNode;
	}

}
