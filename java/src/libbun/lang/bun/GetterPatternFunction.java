package libbun.lang.bun;

import libbun.ast.BGetterNode;
import libbun.ast.BNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class GetterPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode GetterNode = new BGetterNode(ParentNode, LeftNode);
		GetterNode = TokenContext.MatchToken(GetterNode, ".", BTokenContext._Required);
		GetterNode = TokenContext.MatchPattern(GetterNode, BGetterNode._NameInfo, "$Name$", BTokenContext._Required);
		return GetterNode;
	}

}
