package libbun.lang.bun.shell;

import libbun.parser.ast.ZNode;
import libbun.util.ZMatchFunction;
import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;

public class ImportPatternFunction extends ZMatchFunction {
	@Override
	public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		TokenContext.MoveNext();
		ZToken Token = TokenContext.GetToken();
		if(Token.EqualsText("command")) {
			return TokenContext.ParsePattern(ParentNode, ImportCommandPatternFunction._PatternName, ZTokenContext._Required);
		}
		return this.MatchEnvPattern(ParentNode, TokenContext, Token);
	}

	public ZNode MatchEnvPattern(ZNode ParentNode, ZTokenContext TokenContext, ZToken Token) {
//		if(Token.EqualsText("env")) {
//			return TokenContext.ParsePattern(ParentNode, ImportEnvPatternFunc.PatternName, ZTokenContext._Required);
//		}
		return null;	//do not support it
	}
}
