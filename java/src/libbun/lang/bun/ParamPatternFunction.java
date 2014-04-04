package libbun.lang.bun;

import libbun.ast.BLetVarNode;
import libbun.ast.BNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ParamPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ParamNode = new BLetVarNode(ParentNode, BLetVarNode._IsReadOnly, null, null);
		ParamNode = TokenContext.MatchPattern(ParamNode, BLetVarNode._NameInfo, "$Name$", BTokenContext._Required);
		ParamNode = TokenContext.MatchPattern(ParamNode, BLetVarNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		return ParamNode;
	}

}
