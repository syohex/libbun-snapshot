package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZUnaryNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class UnaryPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode UnaryNode = new ZUnaryNode(ParentNode, TokenContext.GetToken(ZTokenContext._MoveNext));
		return TokenContext.MatchPattern(UnaryNode, ZUnaryNode._Recv, "$RightExpression$", ZTokenContext._Required);
	}

}
