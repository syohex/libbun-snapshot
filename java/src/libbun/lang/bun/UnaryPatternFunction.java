package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZUnaryNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class UnaryPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode UnaryNode = new ZUnaryNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext));
		return TokenContext.MatchPattern(UnaryNode, ZUnaryNode._Recv, "$RightExpression$", BTokenContext._Required);
	}

}
