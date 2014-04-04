package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.ZEmptyNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class StatementEndPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var boolean ContextAllowance = TokenContext.SetParseFlag(false);
		@Var BToken Token = null;
		if(TokenContext.HasNext()) {
			Token = TokenContext.GetToken();
			if(!Token.EqualsText(';') && !Token.IsIndent()) {
				TokenContext.SetParseFlag(ContextAllowance);
				return TokenContext.CreateExpectedErrorNode(Token, ";");
			}
			TokenContext.MoveNext();
			while(TokenContext.HasNext()) {
				Token = TokenContext.GetToken();
				if(!Token.EqualsText(';') && !Token.IsIndent()) {
					break;
				}
				TokenContext.MoveNext();
			}
		}
		TokenContext.SetParseFlag(ContextAllowance);
		return new ZEmptyNode(ParentNode, Token);
	}

}
