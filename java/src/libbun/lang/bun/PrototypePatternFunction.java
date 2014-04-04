package libbun.lang.bun;

import libbun.ast.BFunctionNode;
import libbun.ast.BNode;
import libbun.ast.ZPrototypeNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class PrototypePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode FuncNode = new BFunctionNode(ParentNode);
		FuncNode = TokenContext.MatchToken(FuncNode, "function", BTokenContext._Required);
		FuncNode = TokenContext.MatchPattern(FuncNode, BFunctionNode._NameInfo, "$Name$", BTokenContext._Required);
		FuncNode = TokenContext.MatchNtimes(FuncNode, "(", "$Param$", ",", ")");
		FuncNode = TokenContext.MatchPattern(FuncNode, BFunctionNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Required);
		if(FuncNode instanceof BFunctionNode) {
			return new ZPrototypeNode((BFunctionNode)FuncNode);
		}
		return FuncNode;
	}
}


