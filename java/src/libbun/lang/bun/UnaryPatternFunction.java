package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.binary.BUnaryNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class UnaryPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode UnaryNode = new BUnaryNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext));
		return TokenContext.MatchPattern(UnaryNode, BUnaryNode._Recv, "$RightExpression$", BTokenContext._Required);
	}

}
