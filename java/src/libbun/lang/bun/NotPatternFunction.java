package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.unary.BNotNode;
import libbun.ast.unary.BUnaryNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class NotPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode UnaryNode = new BNotNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext));
		UnaryNode = TokenContext.MatchPattern(UnaryNode, BUnaryNode._Recv, "$RightExpression$", BTokenContext._Required);
		return UnaryNode;
	}

}
