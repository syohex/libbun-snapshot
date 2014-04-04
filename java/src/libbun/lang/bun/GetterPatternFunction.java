package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZGetterNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class GetterPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode GetterNode = new ZGetterNode(ParentNode, LeftNode);
		GetterNode = TokenContext.MatchToken(GetterNode, ".", BTokenContext._Required);
		GetterNode = TokenContext.MatchPattern(GetterNode, ZGetterNode._NameInfo, "$Name$", BTokenContext._Required);
		return GetterNode;
	}

}
