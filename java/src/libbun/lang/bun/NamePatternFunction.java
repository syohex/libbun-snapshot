package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.ZGetNameNode;
import libbun.parser.ast.ZNode;
import libbun.util.LibZen;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class NamePatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZToken Token = TokenContext.GetToken(ZTokenContext._MoveNext);
		if(LibZen._IsSymbol(Token.GetChar())) {
			return new ZGetNameNode(ParentNode, Token, Token.GetText());
		}
		return new ZErrorNode(ParentNode, Token, "illegal name: \'" + Token.GetText() + "\'");
	}

}
