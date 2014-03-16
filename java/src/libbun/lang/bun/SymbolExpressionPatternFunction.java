package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.ZGetNameNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class SymbolExpressionPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZToken NameToken = TokenContext.GetToken(ZTokenContext._MoveNext);
		if(TokenContext.IsToken("=")) {
			return new ZErrorNode(ParentNode, TokenContext.GetToken(), "assignment is not en expression");
		}
		else {
			return new ZGetNameNode(ParentNode, NameToken, NameToken.GetText());
		}
	}

}
