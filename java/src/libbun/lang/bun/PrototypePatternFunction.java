package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZPrototypeNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class PrototypePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode FuncNode = new ZFunctionNode(ParentNode);
		FuncNode = TokenContext.MatchToken(FuncNode, "function", ZTokenContext._Required);
		FuncNode = TokenContext.MatchPattern(FuncNode, ZFunctionNode._NameInfo, "$Name$", ZTokenContext._Required);
		FuncNode = TokenContext.MatchNtimes(FuncNode, "(", "$Param$", ",", ")");
		FuncNode = TokenContext.MatchPattern(FuncNode, ZFunctionNode._TypeInfo, "$TypeAnnotation$", ZTokenContext._Required);
		if(FuncNode instanceof ZFunctionNode) {
			return new ZPrototypeNode((ZFunctionNode)FuncNode);
		}
		return FuncNode;
	}
}


