package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.error.ErrorNode;
import libbun.ast.expression.GetNameNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class SymbolExpressionPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken NameToken = TokenContext.GetToken(BTokenContext._MoveNext);
		if(TokenContext.IsToken("=")) {
			return new ErrorNode(ParentNode, TokenContext.GetToken(), "assignment is not en expression");
		}
		else {
			return new GetNameNode(ParentNode, NameToken, NameToken.GetText());
		}
	}

}
