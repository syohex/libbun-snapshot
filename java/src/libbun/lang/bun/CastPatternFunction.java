package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.unary.BCastNode;
import libbun.parser.BTokenContext;
import libbun.type.BType;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class CastPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode CastNode = new BCastNode(ParentNode, BType.VarType, null);
		CastNode = TokenContext.MatchToken(CastNode, "(", BTokenContext._Required);
		CastNode = TokenContext.MatchPattern(CastNode, BCastNode._TypeInfo, "$Type$", BTokenContext._Required);
		CastNode = TokenContext.MatchToken(CastNode, ")", BTokenContext._Required);
		CastNode = TokenContext.MatchPattern(CastNode, BCastNode._Expr, "$RightExpression$", BTokenContext._Required);
		if(CastNode instanceof BCastNode) {
			((BCastNode)CastNode).CastType();  // due to old implementation that cannot be fixed easily.
		}
		return CastNode;
	}

}
