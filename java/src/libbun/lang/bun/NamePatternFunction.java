package libbun.lang.bun;

import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.BGetNameNode;
import libbun.parser.ast.BNode;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class NamePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		if(BLib._IsSymbol(Token.GetChar())) {
			return new BGetNameNode(ParentNode, Token, Token.GetText());
		}
		return new ZErrorNode(ParentNode, Token, "illegal name: \'" + Token.GetText() + "\'");
	}

}
