package libbun.lang.bun.shell;

import libbun.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;

public class RedirectPatternFunction extends BMatchFunction {
	public final static String _PatternName = "$Redirect$";

	// <, >, >>, >&, 1>, 2>, 1>>, 2>>, &>, &>>
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var String RedirectSymbol = Token.GetText();
		if(Token.EqualsText(">>") || Token.EqualsText("<")) {
			return this.CreateRedirectNode(ParentNode, TokenContext, RedirectSymbol, true);
		}
		else if(Token.EqualsText("&")) {
			@Var BToken Token2 = TokenContext.GetToken(BTokenContext._MoveNext);
			if(Token2.EqualsText(">") || Token2.EqualsText(">>")) {
				RedirectSymbol += Token2.GetText();
				return this.CreateRedirectNode(ParentNode, TokenContext, RedirectSymbol, true);
			}
		}
		else if(Token.EqualsText(">")) {
			@Var BToken Token2 = TokenContext.GetToken();
			if(Token2.EqualsText("&")) {
				RedirectSymbol += Token2.GetText();
				return this.CreateRedirectNode(ParentNode, TokenContext, RedirectSymbol, true);
			}
			return this.CreateRedirectNode(ParentNode, TokenContext, RedirectSymbol, true);
		}
		else if(Token.EqualsText("1") || Token.EqualsText("2")) {
			@Var BToken Token2 = TokenContext.GetToken(BTokenContext._MoveNext);
			if(Token2.EqualsText(">>")) {
				RedirectSymbol += Token2.GetText();
				return this.CreateRedirectNode(ParentNode, TokenContext, RedirectSymbol, true);
			}
			else if(Token2.EqualsText(">")) {
				RedirectSymbol += Token2.GetText();
				if(RedirectSymbol.equals("2>") && TokenContext.MatchToken("&")) {
					if(TokenContext.MatchToken("1")) {
						return this.CreateRedirectNode(ParentNode, TokenContext, "2>&1", false);
					}
					return null;
				}
				return this.CreateRedirectNode(ParentNode, TokenContext, RedirectSymbol, true);
			}
		}
		return null;
	}

	private BNode CreateRedirectNode(BNode ParentNode, BTokenContext TokenContext, String RedirectSymbol, boolean existTarget) {
		@Var CommandNode Node = new CommandNode(ParentNode, null, RedirectSymbol);
		if(existTarget) {
			@Var BNode TargetNode = TokenContext.ParsePattern(Node, SimpleArgumentPatternFunction._PatternName, BTokenContext._Required);
			if(TargetNode.IsErrorNode()) {
				return TargetNode;
			}
			Node.AppendArgNode(TargetNode);
		}
		return Node;
	}
}
