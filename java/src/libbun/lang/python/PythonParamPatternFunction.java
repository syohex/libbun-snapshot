package libbun.lang.python;

import libbun.ast.BNode;
import libbun.ast.decl.BLetVarNode;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;
import libbun.util.Var;

public class PythonParamPatternFunction extends BMatchFunction {

	@Override
	public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ParamNode = new BLetVarNode(ParentNode, BLetVarNode._IsReadOnly, null, null);
		ParamNode = TokenContext.MatchPattern(ParamNode, BLetVarNode._NameInfo, "$Name$", BTokenContext._Required);
		return ParamNode;
	}

}
