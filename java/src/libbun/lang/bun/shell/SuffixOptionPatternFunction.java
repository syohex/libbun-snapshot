package libbun.lang.bun.shell;

import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;
import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;

public class SuffixOptionPatternFunction extends BMatchFunction {
	public final static String _PatternName = "$SuffixOption$";

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var ZToken Token = TokenContext.GetToken();
		TokenContext.MoveNext();
		@Var String OptionSymbol = Token.GetText();
		if(Token.EqualsText(ShellUtils._background)) {	// set background job
			return this.CreateNodeAndMatchNextOption(ParentNode, TokenContext, OptionSymbol);
		}
		return null;
	}

	public BNode CreateNodeAndMatchNextOption(BNode ParentNode, ZTokenContext TokenContext, String OptionSymbol) {
		@Var CommandNode Node = new CommandNode(ParentNode, null, OptionSymbol);
		@Var BNode PipedNode = TokenContext.ParsePattern(ParentNode, SuffixOptionPatternFunction._PatternName, ZTokenContext._Optional);
		if(PipedNode != null) {
			Node.AppendPipedNextNode((CommandNode)PipedNode);
		}
		if(!ShellUtils._MatchStopToken(TokenContext)) {
			return new ZErrorNode(ParentNode, TokenContext.GetToken(), "not match stop token");
		}
		return Node;
	}
}
