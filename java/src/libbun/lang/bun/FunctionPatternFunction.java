package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class FunctionPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode FuncNode = new ZFunctionNode(ParentNode);
		FuncNode = TokenContext.MatchToken(FuncNode, "function", BTokenContext._Required);
		FuncNode = TokenContext.MatchPattern(FuncNode, ZFunctionNode._NameInfo, "$Name$", BTokenContext._Optional);
		FuncNode = TokenContext.MatchNtimes(FuncNode, "(", "$Param$", ",", ")");
		FuncNode = TokenContext.MatchPattern(FuncNode, ZFunctionNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		FuncNode = TokenContext.MatchPattern(FuncNode, ZFunctionNode._Block, "$Block$", BTokenContext._Required);
		return FuncNode;
	}

}
