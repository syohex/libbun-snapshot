package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.BGetNameNode;
import libbun.parser.ast.BNode;
import libbun.util.LibZen;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class NamePatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var ZToken Token = TokenContext.GetToken(ZTokenContext._MoveNext);
		if(LibZen._IsSymbol(Token.GetChar())) {
			return new BGetNameNode(ParentNode, Token, Token.GetText());
		}
		return new ZErrorNode(ParentNode, Token, "illegal name: \'" + Token.GetText() + "\'");
	}

}
