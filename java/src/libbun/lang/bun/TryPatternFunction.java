package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZTryNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class TryPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode TryNode = new ZTryNode(ParentNode);
		TryNode = TokenContext.MatchToken(TryNode, "try", ZTokenContext._Required);
		TryNode = TokenContext.MatchPattern(TryNode, ZTryNode._Try, "$Block$", ZTokenContext._Required);
		@Var int count = 0;
		if(TokenContext.MatchNewLineToken("catch")) {
			TryNode = TokenContext.MatchToken(TryNode, "(", ZTokenContext._Required);
			TryNode = TokenContext.MatchPattern(TryNode, ZTryNode._NameInfo, "$Name$", ZTokenContext._Required);
			TryNode = TokenContext.MatchToken(TryNode, ")", ZTokenContext._Required);
			TryNode = TokenContext.MatchPattern(TryNode, ZTryNode._Catch, "$Block$", ZTokenContext._Required);
			count = count + 1;
		}
		if(TokenContext.MatchNewLineToken("finally")) {
			TryNode = TokenContext.MatchPattern(TryNode, ZTryNode._Finally, "$Block$", ZTokenContext._Required);
			count = count + 1;
		}
		if(count == 0 && !TryNode.IsErrorNode()) {
			TryNode = new ZErrorNode(ParentNode, TryNode.SourceToken, "either catch or finally is expected");
		}
		return TryNode;
	}

}
