package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BLetVarNode;
import libbun.parser.ast.BNode;
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
