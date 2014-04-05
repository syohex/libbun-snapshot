package libbun.lang.bun.shell;

import libbun.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;

public class SuffixOptionPatternFunction extends BMatchFunction {
	public final static String _PatternName = "$SuffixOption$";

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken();
		TokenContext.MoveNext();
		@Var String OptionSymbol = Token.GetText();
		if(Token.EqualsText(ShellUtils._background)) {	// set background job
			return new CommandNode(ParentNode, Token, OptionSymbol);
		}
		return null;
	}
}
