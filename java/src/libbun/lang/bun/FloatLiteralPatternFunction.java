package libbun.lang.bun;

import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.parser.ast.BFloatNode;
import libbun.parser.ast.BNode;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class FloatLiteralPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		return new BFloatNode(ParentNode, Token, BLib._ParseFloat(Token.GetText()));
	}

}
