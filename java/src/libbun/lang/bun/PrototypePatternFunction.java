package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.decl.BunFunctionNode;
import libbun.ast.decl.ZPrototypeNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class PrototypePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode FuncNode = new BunFunctionNode(ParentNode);
		FuncNode = TokenContext.MatchToken(FuncNode, "function", BTokenContext._Required);
		FuncNode = TokenContext.MatchPattern(FuncNode, BunFunctionNode._NameInfo, "$Name$", BTokenContext._Required);
		FuncNode = TokenContext.MatchNtimes(FuncNode, "(", "$Param$", ",", ")");
		FuncNode = TokenContext.MatchPattern(FuncNode, BunFunctionNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Required);
		if(FuncNode instanceof BunFunctionNode) {
			return new ZPrototypeNode((BunFunctionNode)FuncNode);
		}
		return FuncNode;
	}
}


