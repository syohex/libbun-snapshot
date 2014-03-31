package libbun.lang.bun.shell;

import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZStringNode;
import libbun.util.LibZen;
import libbun.util.Var;
import libbun.util.ZArray;
import libbun.util.ZMatchFunction;
import libbun.parser.ZPatternToken;
import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;

public class SimpleArgumentPatternFunction extends ZMatchFunction {	// subset of CommandArgPatternFunc
	public final static String _PatternName = "$CommandArg$";

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		if(ShellUtils._MatchStopToken(TokenContext)) {
			return null;
		}
		@Var boolean FoundSubstitution = false;
		@Var boolean FoundEscape = false;
		@Var ZArray<ZToken> TokenList = new ZArray<ZToken>(new ZToken[]{});
		@Var ZArray<ZNode> NodeList = new ZArray<ZNode>(new ZNode[]{});
		while(!ShellUtils._MatchStopToken(TokenContext)) {
			@Var ZToken Token = TokenContext.GetToken(ZTokenContext._MoveNext);
			if(Token instanceof ZPatternToken && ((ZPatternToken)Token).PresetPattern.equals("$StringLiteral$")) {
				this.Flush(TokenContext, NodeList, TokenList);
				NodeList.add(new ZStringNode(ParentNode, null, LibZen._UnquoteString(Token.GetText())));
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
		@Var ZNode ArgNode = new ArgumentNode(ParentNode, FoundSubstitution ? ArgumentNode._Substitution : ArgumentNode._Normal);
		ArgNode.SetNode(ArgumentNode._Expr, ShellUtils._ToNode(ParentNode, TokenContext, NodeList));
		return ArgNode;
	}

	private boolean CheckEscape(ZToken Token, boolean FoundEscape) {
		if(Token.EqualsText("\\") && !FoundEscape) {
			return true;
		}
		return false;
	}

	private void Flush(ZTokenContext TokenContext, ZArray<ZNode> NodeList, ZArray<ZToken> TokenList) {
		@Var int size = TokenList.size();
		if(size == 0) {
			return;
		}
		@Var int StartIndex = 0;
		@Var int EndIndex = 0;
		for(int i = 0; i < size; i++) {
			if(i == 0) {
				StartIndex = ZArray.GetIndex(TokenList, i).StartIndex;
			}
			if(i == size - 1) {
				EndIndex = ZArray.GetIndex(TokenList, i).EndIndex;
			}
		}
		@Var ZToken Token = new ZToken(TokenContext.Source, StartIndex, EndIndex);
		NodeList.add(new ZStringNode(null, Token, LibZen._UnquoteString(this.ResolveHome(Token.GetText()))));
		TokenList.clear(0);
	}

	public String ResolveHome(String Path) {
		return ShellUtils._ResolveHome(Path);
	}
}
