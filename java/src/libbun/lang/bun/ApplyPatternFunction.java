package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZFuncCallNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class ApplyPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode ApplyNode = new ZFuncCallNode(ParentNode, LeftNode);
		ApplyNode = TokenContext.MatchNtimes(ApplyNode, "(", "$Expression$", ",", ")");
		return ApplyNode;
	}

}
