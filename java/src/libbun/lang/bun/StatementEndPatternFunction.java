package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZEmptyNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class StatementEndPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var boolean ContextAllowance = TokenContext.SetParseFlag(false);
		@Var ZToken Token = null;
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
