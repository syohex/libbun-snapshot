package libbun.lang.bun.shell;

import libbun.ast.BNode;
import libbun.ast.literal.BStringNode;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.BArray;
import libbun.util.BMatchFunction;
import libbun.parser.BPatternToken;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;

public class SimpleArgumentPatternFunction extends BMatchFunction {	// subset of CommandArgPatternFunc
	public final static String _PatternName = "$CommandArg$";

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		if(ShellUtils._MatchStopToken(TokenContext)) {
			return null;
		}
		@Var boolean FoundSubstitution = false;
		@Var boolean FoundEscape = false;
		@Var BArray<BToken> TokenList = new BArray<BToken>(new BToken[]{});
		@Var BArray<BNode> NodeList = new BArray<BNode>(new BNode[]{});
		while(!ShellUtils._MatchStopToken(TokenContext)) {
			@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
			if(Token instanceof BPatternToken && ((BPatternToken)Token).PresetPattern.equals("$StringLiteral$")) {
				this.Flush(TokenContext, NodeList, TokenList);
				NodeList.add(new BStringNode(ParentNode, null, BLib._UnquoteString(Token.GetText())));
			}
			else {
				TokenList.add(Token);
			}
			if(Token.IsNextWhiteSpace()) {
				break;
			}
			FoundEscape = this.CheckEscape(Token, FoundEscape);
		}
		this.Flush(TokenContext, NodeList, TokenList);
		@Var BNode ArgNode = new ArgumentNode(ParentNode, FoundSubstitution ? ArgumentNode._Substitution : ArgumentNode._Normal);
		ArgNode.SetNode(ArgumentNode._Expr, ShellUtils._ToNode(ParentNode, TokenContext, NodeList));
		return ArgNode;
	}

	private boolean CheckEscape(BToken Token, boolean FoundEscape) {
		if(Token.EqualsText("\\") && !FoundEscape) {
			return true;
		}
		return false;
	}

	private void Flush(BTokenContext TokenContext, BArray<BNode> NodeList, BArray<BToken> TokenList) {
		@Var int size = TokenList.size();
		if(size == 0) {
			return;
		}
		@Var int StartIndex = 0;
		@Var int EndIndex = 0;
		for(int i = 0; i < size; i++) {
			if(i == 0) {
				StartIndex = BArray.GetIndex(TokenList, i).StartIndex;
			}
			if(i == size - 1) {
				EndIndex = BArray.GetIndex(TokenList, i).EndIndex;
			}
		}
		@Var BToken Token = new BToken(TokenContext.Source, StartIndex, EndIndex);
		NodeList.add(new BStringNode(null, Token, BLib._UnquoteString(this.ResolveHome(Token.GetText()))));
		TokenList.clear(0);
	}

	public String ResolveHome(String Path) {
		return ShellUtils._ResolveHome(Path);
	}
}
