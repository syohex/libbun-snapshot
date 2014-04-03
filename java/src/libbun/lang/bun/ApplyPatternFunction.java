package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZFuncCallNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class ApplyPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ApplyNode = new ZFuncCallNode(ParentNode, LeftNode);
		ApplyNode = TokenContext.MatchNtimes(ApplyNode, "(", "$Expression$", ",", ")");
		return ApplyNode;
	}

}
