package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.literal.BIntNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class IntLiteralPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		return new BIntNode(ParentNode, Token, BLib._ParseInt(Token.GetText()));
	}

}
