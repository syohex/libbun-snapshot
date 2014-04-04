package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BStringNode;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class StringLiteralPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var ZToken Token = TokenContext.GetToken(ZTokenContext._MoveNext);
		return new BStringNode(ParentNode, Token, BLib._UnquoteString(Token.GetText()));
	}
}
