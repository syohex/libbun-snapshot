package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZIntNode;
import libbun.parser.ast.ZNode;
import libbun.util.LibZen;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class IntLiteralPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZToken Token = TokenContext.GetToken(ZTokenContext._MoveNext);
		return new ZIntNode(ParentNode, Token, LibZen._ParseInt(Token.GetText()));
	}

}
