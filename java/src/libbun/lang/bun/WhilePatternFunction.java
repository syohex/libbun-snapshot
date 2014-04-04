package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZWhileNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class WhilePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode WhileNode = new ZWhileNode(ParentNode);
		WhileNode = TokenContext.MatchToken(WhileNode, "while", ZTokenContext._Required);
		WhileNode = TokenContext.MatchToken(WhileNode, "(", ZTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, ZWhileNode._Cond, "$Expression$", ZTokenContext._Required, ZTokenContext._AllowSkipIndent);
		if(TokenContext.MatchNewLineToken("whatever")) {
			WhileNode = TokenContext.MatchPattern(WhileNode, ZWhileNode._Next, "$InStatement$", ZTokenContext._Required, ZTokenContext._AllowSkipIndent);
		}
		WhileNode = TokenContext.MatchToken(WhileNode, ")", ZTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, ZWhileNode._Block, "$Block$", ZTokenContext._Required);
		return WhileNode;
	}

}
