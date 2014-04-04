package libbun.lang.bun.shell;

import libbun.parser.ast.BNode;
import libbun.util.BMatchFunction;
import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;

public class ImportPatternFunction extends BMatchFunction {
	@Override
	public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		TokenContext.MoveNext();
		ZToken Token = TokenContext.GetToken();
		if(Token.EqualsText("command")) {
			return TokenContext.ParsePattern(ParentNode, ImportCommandPatternFunction._PatternName, ZTokenContext._Required);
		}
		return this.MatchEnvPattern(ParentNode, TokenContext, Token);
	}

	public BNode MatchEnvPattern(BNode ParentNode, ZTokenContext TokenContext, ZToken Token) {
//		if(Token.EqualsText("env")) {
//			return TokenContext.ParsePattern(ParentNode, ImportEnvPatternFunc.PatternName, ZTokenContext._Required);
//		}
		return null;	//do not support it
	}
}
