package libbun.lang.bun.shell;

import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;
import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;

public class SuffixOptionPatternFunction extends ZMatchFunction {
	public final static String _PatternName = "$SuffixOption$";

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZToken Token = TokenContext.GetToken();
		TokenContext.MoveNext();
		@Var String OptionSymbol = Token.GetText();
		if(Token.EqualsText(ShellUtils._background)) {	// set background job
			return this.CreateNodeAndMatchNextOption(ParentNode, TokenContext, OptionSymbol);
		}
		return null;
	}

	public ZNode CreateNodeAndMatchNextOption(ZNode ParentNode, ZTokenContext TokenContext, String OptionSymbol) {
		@Var CommandNode Node = new CommandNode(ParentNode, null, OptionSymbol);
		@Var ZNode PipedNode = TokenContext.ParsePattern(ParentNode, SuffixOptionPatternFunction._PatternName, ZTokenContext._Optional);
		if(PipedNode != null) {
			Node.AppendPipedNextNode((CommandNode)PipedNode);
		}
		if(!ShellUtils._MatchStopToken(TokenContext)) {
			return new ZErrorNode(ParentNode, TokenContext.GetToken(), "not match stop token");
		}
		return Node;
	}
}
