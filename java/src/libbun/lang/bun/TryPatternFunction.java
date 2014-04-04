package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZTryNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class TryPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode TryNode = new ZTryNode(ParentNode);
		TryNode = TokenContext.MatchToken(TryNode, "try", BTokenContext._Required);
		TryNode = TokenContext.MatchPattern(TryNode, ZTryNode._Try, "$Block$", BTokenContext._Required);
		@Var int count = 0;
		if(TokenContext.MatchNewLineToken("catch")) {
			TryNode = TokenContext.MatchToken(TryNode, "(", BTokenContext._Required);
			TryNode = TokenContext.MatchPattern(TryNode, ZTryNode._NameInfo, "$Name$", BTokenContext._Required);
			TryNode = TokenContext.MatchToken(TryNode, ")", BTokenContext._Required);
			TryNode = TokenContext.MatchPattern(TryNode, ZTryNode._Catch, "$Block$", BTokenContext._Required);
			count = count + 1;
		}
		if(TokenContext.MatchNewLineToken("finally")) {
			TryNode = TokenContext.MatchPattern(TryNode, ZTryNode._Finally, "$Block$", BTokenContext._Required);
			count = count + 1;
		}
		if(count == 0 && !TryNode.IsErrorNode()) {
			TryNode = new ZErrorNode(ParentNode, TryNode.SourceToken, "either catch or finally is expected");
		}
		return TryNode;
	}

}
