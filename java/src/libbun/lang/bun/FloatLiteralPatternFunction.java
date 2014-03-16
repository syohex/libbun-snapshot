package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZFloatNode;
import libbun.parser.ast.ZNode;
import libbun.util.LibZen;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class FloatLiteralPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZToken Token = TokenContext.GetToken(ZTokenContext._MoveNext);
		return new ZFloatNode(ParentNode, Token, LibZen._ParseFloat(Token.GetText()));
	}

}
