package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.expression.FuncCallNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ApplyPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ApplyNode = new FuncCallNode(ParentNode, LeftNode);
		ApplyNode = TokenContext.MatchNtimes(ApplyNode, "(", "$Expression$", ",", ")");
		return ApplyNode;
	}

}
