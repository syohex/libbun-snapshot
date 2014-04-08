package libbun.lang.python;

import libbun.ast.BNode;
import libbun.ast.decl.BFunctionNode;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;
import libbun.util.Var;

public class PythonFunctionPatternFunction extends BMatchFunction {
	@Override
	public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode FuncNode = new BFunctionNode(ParentNode);
		FuncNode = TokenContext.MatchToken(FuncNode, "def", BTokenContext._Required);
		FuncNode = TokenContext.MatchPattern(FuncNode, BFunctionNode._NameInfo, "$Name$", BTokenContext._Optional);
		FuncNode = TokenContext.MatchNtimes(FuncNode, "(", "$Param$", ",", ")");
		FuncNode = TokenContext.MatchPattern(FuncNode, BFunctionNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		FuncNode = TokenContext.MatchPattern(FuncNode, BFunctionNode._Block, "$Block$", BTokenContext._Required);
		return FuncNode;
	}
}
