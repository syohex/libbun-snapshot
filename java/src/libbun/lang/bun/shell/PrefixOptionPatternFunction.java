package libbun.lang.bun.shell;

import libbun.parser.ast.BNode;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.BMatchFunction;
import libbun.parser.BPatternToken;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;

public class PrefixOptionPatternFunction extends BMatchFunction {
	public final static String _PatternName = "$PrefixOption$";

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var String Symbol = Token.GetText();
		if(Symbol.equals(ShellUtils._trace)) {
			@Var BNode CommandNode = TokenContext.ParsePattern(ParentNode, CommandSymbolPatternFunction._PatternName, BTokenContext._Required);
			if(CommandNode.IsErrorNode()) {
				return CommandNode;
			}
			@Var CommandNode Node = new CommandNode(ParentNode, Token, Symbol);
			return Node.AppendPipedNextNode((CommandNode) CommandNode);
		}
		if(Symbol.equals(ShellUtils._timeout) && LeftNode == null) {
			@Var BNode TimeNode = this.ParseTimeout(ParentNode, TokenContext);
			if(TimeNode.IsErrorNode()) {
				return TimeNode;
			}
			@Var BNode CommandNode = TokenContext.ParsePattern(ParentNode, CommandSymbolPatternFunction._PatternName, BTokenContext._Required);
			if(CommandNode.IsErrorNode()) {
				return CommandNode;
			}
			@Var CommandNode Node = new CommandNode(ParentNode, Token, Symbol);
			Node.AppendArgNode(TimeNode);
			return Node.AppendPipedNextNode((CommandNode) CommandNode);
		}
		return null;
	}

	public BNode ParseTimeout(BNode ParentNode, BTokenContext TokenContext) {
		@Var BToken NumToken = TokenContext.GetToken(BTokenContext._MoveNext);
		if((NumToken instanceof BPatternToken)) {
			if(((BPatternToken)NumToken).PresetPattern.PatternName.equals(("$IntegerLiteral$"))) {
				@Var long Num = BLib._ParseInt(NumToken.GetText());
				if(Num > 0) {
					if(NumToken.IsNextWhiteSpace()) {
						return new ArgumentNode(ParentNode, Long.toString(Num));
					}
					@Var BToken UnitToken = TokenContext.GetToken(BTokenContext._MoveNext);
					@Var String UnitSymbol = UnitToken.GetText();
					if(UnitSymbol.equals("ms")) {
						return new ArgumentNode(ParentNode, Long.toString(Num));
					}
					if(UnitSymbol.equals("s")) {
						return new ArgumentNode(ParentNode, Long.toString(Num * 1000));
					}
					if(UnitSymbol.equals("m")) {
						return new ArgumentNode(ParentNode, Long.toString(Num * 1000 * 60));
					}
					return TokenContext.CreateExpectedErrorNode(UnitToken, "{ms, s, m}");
				}
			}
		}
		return TokenContext.CreateExpectedErrorNode(NumToken, "Integer Number Symbol");
	}
}
