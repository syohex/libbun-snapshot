package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZLetVarNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class ParamPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode ParamNode = new ZLetVarNode(ParentNode, ZLetVarNode._ReadOnly);
		ParamNode = TokenContext.MatchPattern(ParamNode, ZLetVarNode._NameInfo, "$Name$", ZTokenContext._Required);
		ParamNode = TokenContext.MatchPattern(ParamNode, ZLetVarNode._TypeInfo, "$TypeAnnotation$", ZTokenContext._Optional);
		return ParamNode;
	}

}
