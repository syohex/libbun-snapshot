package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.error.ErrorNode;
import libbun.ast.expression.GetNameNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class NamePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		if(BLib._IsSymbol(Token.GetChar())) {
			return new GetNameNode(ParentNode, Token, Token.GetText());
		}
		return new ErrorNode(ParentNode, Token, "illegal name: \'" + Token.GetText() + "\'");
	}

}
