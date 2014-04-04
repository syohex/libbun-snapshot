package libbun.lang.konoha;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.sugar.ZContinueNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ContinuePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ContinueNode = new ZContinueNode(ParentNode);
		ContinueNode = TokenContext.MatchToken(ContinueNode, "continue", BTokenContext._Required);
		return ContinueNode;
	}

}
