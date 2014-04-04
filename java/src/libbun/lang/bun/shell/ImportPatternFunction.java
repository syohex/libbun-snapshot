package libbun.lang.bun.shell;

import libbun.parser.ast.BNode;
import libbun.util.BMatchFunction;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;

public class ImportPatternFunction extends BMatchFunction {
	@Override
	public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		TokenContext.MoveNext();
		BToken Token = TokenContext.GetToken();
		if(Token.EqualsText("command")) {
			return TokenContext.ParsePattern(ParentNode, ImportCommandPatternFunction._PatternName, BTokenContext._Required);
		}
		return this.MatchEnvPattern(ParentNode, TokenContext, Token);
	}

	public BNode MatchEnvPattern(BNode ParentNode, BTokenContext TokenContext, BToken Token) {
//		if(Token.EqualsText("env")) {
//			return TokenContext.ParsePattern(ParentNode, ImportEnvPatternFunc.PatternName, ZTokenContext._Required);
//		}
		return null;	//do not support it
	}
}
