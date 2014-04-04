package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.error.BErrorNode;
import libbun.ast.expression.BGetNameNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class NamePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		if(BLib._IsSymbol(Token.GetChar())) {
			return new BGetNameNode(ParentNode, Token, Token.GetText());
		}
		return new BErrorNode(ParentNode, Token, "illegal name: \'" + Token.GetText() + "\'");
	}

}
