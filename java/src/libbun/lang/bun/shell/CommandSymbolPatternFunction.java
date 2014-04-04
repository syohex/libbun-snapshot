package libbun.lang.bun.shell;

import libbun.ast.BErrorNode;
import libbun.ast.BNode;
import libbun.ast.BStringNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;

public class CommandSymbolPatternFunction extends BMatchFunction {
	public final static String _PatternName = "$CommandSymbol$";

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken CommandToken = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var BNode SymbolNode = ParentNode.GetNameSpace().GetSymbol(ShellUtils._ToCommandSymbol(CommandToken.GetText()));
		if(SymbolNode == null || !(SymbolNode instanceof BStringNode)) {
			return new BErrorNode(ParentNode, CommandToken, "undefined command symbol");
		}
		@Var String Command = ((BStringNode)SymbolNode).StringValue;
		@Var CommandNode CommandNode = new CommandNode(ParentNode, CommandToken, Command);
		while(TokenContext.HasNext()) {
			if(TokenContext.MatchToken("|")) {
				// Match Prefix Option
				@Var BNode PrefixOptionNode = TokenContext.ParsePatternAfter(ParentNode, CommandNode, PrefixOptionPatternFunction._PatternName, BTokenContext._Optional);
				if(PrefixOptionNode != null) {
					return CommandNode.AppendPipedNextNode((CommandNode)PrefixOptionNode);
				}
				// Match Command Symbol
				@Var BNode PipedNode = TokenContext.ParsePattern(ParentNode, CommandSymbolPatternFunction._PatternName, BTokenContext._Required);
				if(PipedNode.IsErrorNode()) {
					return PipedNode;
				}
				return CommandNode.AppendPipedNextNode((CommandNode)PipedNode);
			}
			// Match Redirect
			@Var BNode RedirectNode = TokenContext.ParsePattern(ParentNode, RedirectPatternFunction._PatternName, BTokenContext._Optional);
			if(RedirectNode != null) {
				CommandNode.AppendPipedNextNode((CommandNode)RedirectNode);
				continue;
			}
			// Match Suffix Option
			@Var BNode SuffixOptionNode = TokenContext.ParsePattern(ParentNode, SuffixOptionPatternFunction._PatternName, BTokenContext._Optional);
			if(SuffixOptionNode != null) {
				if(SuffixOptionNode.IsErrorNode()) {
					return SuffixOptionNode;
				}
				return CommandNode.AppendPipedNextNode((CommandNode)SuffixOptionNode);
			}
			// Match Argument
			@Var BNode ArgNode = TokenContext.ParsePattern(ParentNode, SimpleArgumentPatternFunction._PatternName, BTokenContext._Optional);
			if(ArgNode == null) {
				break;
			}
			CommandNode.AppendArgNode(ArgNode);
		}
		return CommandNode;
	}
}
