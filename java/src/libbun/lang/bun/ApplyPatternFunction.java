package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.ZFuncCallNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ApplyPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ApplyNode = new ZFuncCallNode(ParentNode, LeftNode);
		ApplyNode = TokenContext.MatchNtimes(ApplyNode, "(", "$Expression$", ",", ")");
		return ApplyNode;
	}

}
