package libbun.lang.konoha;

import libbun.ast.BNode;
import libbun.ast.sugar.ZContinueNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ContinuePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ContinueNode = new ZContinueNode(ParentNode);
		ContinueNode = TokenContext.MatchToken(ContinueNode, "continue", BTokenContext._Required);
		return ContinueNode;
	}

}
