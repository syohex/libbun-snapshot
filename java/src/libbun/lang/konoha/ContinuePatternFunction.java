package libbun.lang.konoha;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.sugar.ZContinueNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ContinuePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ContinueNode = new ZContinueNode(ParentNode);
		ContinueNode = TokenContext.MatchToken(ContinueNode, "continue", ZTokenContext._Required);
		return ContinueNode;
	}

}
