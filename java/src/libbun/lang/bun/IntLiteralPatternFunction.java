package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.BIntNode;
import libbun.parser.ast.BNode;
import libbun.util.LibZen;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class IntLiteralPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var ZToken Token = TokenContext.GetToken(ZTokenContext._MoveNext);
		return new BIntNode(ParentNode, Token, LibZen._ParseInt(Token.GetText()));
	}

}
