package libbun.lang.bun;

import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.BGetNameNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class SymbolExpressionPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken NameToken = TokenContext.GetToken(BTokenContext._MoveNext);
		if(TokenContext.IsToken("=")) {
			return new ZErrorNode(ParentNode, TokenContext.GetToken(), "assignment is not en expression");
		}
		else {
			return new BGetNameNode(ParentNode, NameToken, NameToken.GetText());
		}
	}

}
