package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZParamNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class ParamPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode ParamNode = new ZParamNode(ParentNode);
		ParamNode = TokenContext.MatchPattern(ParamNode, ZParamNode._NameInfo, "$Name$", ZTokenContext._Required);
		ParamNode = TokenContext.MatchPattern(ParamNode, ZParamNode._TypeInfo, "$TypeAnnotation$", ZTokenContext._Optional);
		return ParamNode;
	}

}
