package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZGetNameNode;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZSetNameNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class SymbolStatementPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZToken NameToken = TokenContext.GetToken(ZTokenContext._MoveNext);
		if(TokenContext.MatchToken("=")) {
			@Var ZNode AssignedNode = new ZSetNameNode(ParentNode, NameToken, NameToken.GetText());
			AssignedNode = TokenContext.MatchPattern(AssignedNode, ZSetNameNode._Expr, "$Expression$", ZTokenContext._Required);
			return AssignedNode;
		}
		else {
			return new ZGetNameNode(ParentNode, NameToken, NameToken.GetText());
		}
	}

}
