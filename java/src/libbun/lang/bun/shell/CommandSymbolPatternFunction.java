package libbun.lang.bun.shell;

import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BStringNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;
import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;

public class CommandSymbolPatternFunction extends BMatchFunction {
	public final static String _PatternName = "$CommandSymbol$";

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var ZToken CommandToken = TokenContext.GetToken(ZTokenContext._MoveNext);
		@Var BNode SymbolNode = ParentNode.GetNameSpace().GetSymbol(ShellUtils._ToCommandSymbol(CommandToken.GetText()));
		if(SymbolNode == null || !(SymbolNode instanceof BStringNode)) {
			return new ZErrorNode(ParentNode, CommandToken, "undefined command symbol");
		}
		@Var String Command = ((BStringNode)SymbolNode).StringValue;
		@Var CommandNode CommandNode = new CommandNode(ParentNode, CommandToken, Command);
		while(TokenContext.HasNext()) {
			if(TokenContext.MatchToken("|")) {
				// Match Prefix Option
				@Var BNode PrefixOptionNode = TokenContext.ParsePatternAfter(ParentNode, CommandNode, PrefixOptionPatternFunction._PatternName, ZTokenContext._Optional);
				if(PrefixOptionNode != null) {
					return CommandNode.AppendPipedNextNode((CommandNode)PrefixOptionNode);
				}
				// Match Command Symbol
				@Var BNode PipedNode = TokenContext.ParsePattern(ParentNode, CommandSymbolPatternFunction._PatternName, ZTokenContext._Required);
				if(PipedNode.IsErrorNode()) {
					return PipedNode;
				}
				return CommandNode.AppendPipedNextNode((CommandNode)PipedNode);
			}
			// Match Redirect
			@Var BNode RedirectNode = TokenContext.ParsePattern(ParentNode, RedirectPatternFunction._PatternName, ZTokenContext._Optional);
			if(RedirectNode != null) {
				CommandNode.AppendPipedNextNode((CommandNode)RedirectNode);
				continue;
			}
			// Match Suffix Option
			@Var BNode SuffixOptionNode = TokenContext.ParsePattern(ParentNode, SuffixOptionPatternFunction._PatternName, ZTokenContext._Optional);
			if(SuffixOptionNode != null) {
				if(SuffixOptionNode.IsErrorNode()) {
					return SuffixOptionNode;
				}
				return CommandNode.AppendPipedNextNode((CommandNode)SuffixOptionNode);
			}
			// Match Argument
			@Var BNode ArgNode = TokenContext.ParsePattern(ParentNode, SimpleArgumentPatternFunction._PatternName, ZTokenContext._Optional);
			if(ArgNode == null) {
				break;
			}
			CommandNode.AppendArgNode(ArgNode);
		}
		return CommandNode;
	}
}
