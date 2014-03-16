package libbun.lang.konoha;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNode;
import libbun.parser.sugar.ZContinueNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class ContinuePatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode ContinueNode = new ZContinueNode(ParentNode);
		ContinueNode = TokenContext.MatchToken(ContinueNode, "continue", ZTokenContext._Required);
		return ContinueNode;
	}

}
