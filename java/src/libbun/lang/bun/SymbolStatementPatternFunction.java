package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.BGetNameNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BSetNameNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class SymbolStatementPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var ZToken NameToken = TokenContext.GetToken(ZTokenContext._MoveNext);
		@Var BGetNameNode NameNode = new BGetNameNode(ParentNode, NameToken, NameToken.GetText());
		if(TokenContext.IsToken("=")) {
			@Var BNode AssignedNode = new BSetNameNode(ParentNode, null, NameNode);
			AssignedNode = TokenContext.MatchToken(ParentNode, "=", ZTokenContext._Required);
			AssignedNode = TokenContext.MatchPattern(AssignedNode, BSetNameNode._Expr, "$Expression$", ZTokenContext._Required);
			return AssignedNode;
		}
		else {
			return NameNode;
		}
	}

}
