package libbun.lang.bun.shell;

import libbun.parser.ast.BNode;
import libbun.parser.ast.BStringNode;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.BArray;
import libbun.util.BMatchFunction;
import libbun.parser.ZPatternToken;
import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;

public class SimpleArgumentPatternFunction extends BMatchFunction {	// subset of CommandArgPatternFunc
	public final static String _PatternName = "$CommandArg$";

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		if(ShellUtils._MatchStopToken(TokenContext)) {
			return null;
		}
		@Var boolean FoundSubstitution = false;
		@Var boolean FoundEscape = false;
		@Var BArray<ZToken> TokenList = new BArray<ZToken>(new ZToken[]{});
		@Var BArray<BNode> NodeList = new BArray<BNode>(new BNode[]{});
		while(!ShellUtils._MatchStopToken(TokenContext)) {
			@Var ZToken Token = TokenContext.GetToken(ZTokenContext._MoveNext);
			if(Token instanceof ZPatternToken && ((ZPatternToken)Token).PresetPattern.equals("$StringLiteral$")) {
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

	private boolean CheckEscape(ZToken Token, boolean FoundEscape) {
		if(Token.EqualsText("\\") && !FoundEscape) {
			return true;
		}
		return false;
	}

	private void Flush(ZTokenContext TokenContext, BArray<BNode> NodeList, BArray<ZToken> TokenList) {
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
		@Var ZToken Token = new ZToken(TokenContext.Source, StartIndex, EndIndex);
		NodeList.add(new BStringNode(null, Token, BLib._UnquoteString(this.ResolveHome(Token.GetText()))));
		TokenList.clear(0);
	}

	public String ResolveHome(String Path) {
		return ShellUtils._ResolveHome(Path);
	}
}
