package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZCastNode;
import libbun.parser.ast.BNode;
import libbun.type.BType;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class CastPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode CastNode = new ZCastNode(ParentNode, BType.VarType, null);
		CastNode = TokenContext.MatchToken(CastNode, "(", BTokenContext._Required);
		CastNode = TokenContext.MatchPattern(CastNode, ZCastNode._TypeInfo, "$Type$", BTokenContext._Required);
		CastNode = TokenContext.MatchToken(CastNode, ")", BTokenContext._Required);
		CastNode = TokenContext.MatchPattern(CastNode, ZCastNode._Expr, "$RightExpression$", BTokenContext._Required);
		if(CastNode instanceof ZCastNode) {
			((ZCastNode)CastNode).CastType();  // due to old implementation that cannot be fixed easily.
		}
		return CastNode;
	}

}
