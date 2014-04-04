package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZWhileNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class WhilePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode WhileNode = new ZWhileNode(ParentNode);
		WhileNode = TokenContext.MatchToken(WhileNode, "while", BTokenContext._Required);
		WhileNode = TokenContext.MatchToken(WhileNode, "(", BTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, ZWhileNode._Cond, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		if(TokenContext.MatchNewLineToken("whatever")) {
			WhileNode = TokenContext.MatchPattern(WhileNode, ZWhileNode._Next, "$InStatement$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		}
		WhileNode = TokenContext.MatchToken(WhileNode, ")", BTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, ZWhileNode._Block, "$Block$", BTokenContext._Required);
		return WhileNode;
	}

}
