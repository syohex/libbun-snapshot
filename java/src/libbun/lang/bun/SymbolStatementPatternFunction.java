package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.expression.GetNameNode;
import libbun.ast.expression.SetNameNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;
import libbun.util.Var;

public class SymbolStatementPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken NameToken = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var GetNameNode NameNode = new GetNameNode(ParentNode, NameToken, NameToken.GetText());
		if(TokenContext.IsToken("=")) {
			@Var BNode AssignedNode = new SetNameNode(ParentNode, null, NameNode);
			AssignedNode = TokenContext.MatchToken(AssignedNode, "=", BTokenContext._Required);
			AssignedNode = TokenContext.MatchPattern(AssignedNode, SetNameNode._Expr, "$Expression$", BTokenContext._Required);
			return AssignedNode;
		}
		else {
			return NameNode;
		}
	}

}
