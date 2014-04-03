package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BLetVarNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class ParamPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ParamNode = new BLetVarNode(ParentNode, BLetVarNode._IsReadOnly, null, null);
		ParamNode = TokenContext.MatchPattern(ParamNode, BLetVarNode._NameInfo, "$Name$", ZTokenContext._Required);
		ParamNode = TokenContext.MatchPattern(ParamNode, BLetVarNode._TypeInfo, "$TypeAnnotation$", ZTokenContext._Optional);
		return ParamNode;
	}

}
