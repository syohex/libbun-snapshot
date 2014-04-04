package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZPrototypeNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class PrototypePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode FuncNode = new ZFunctionNode(ParentNode);
		FuncNode = TokenContext.MatchToken(FuncNode, "function", BTokenContext._Required);
		FuncNode = TokenContext.MatchPattern(FuncNode, ZFunctionNode._NameInfo, "$Name$", BTokenContext._Required);
		FuncNode = TokenContext.MatchNtimes(FuncNode, "(", "$Param$", ",", ")");
		FuncNode = TokenContext.MatchPattern(FuncNode, ZFunctionNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Required);
		if(FuncNode instanceof ZFunctionNode) {
			return new ZPrototypeNode((ZFunctionNode)FuncNode);
		}
		return FuncNode;
	}
}


