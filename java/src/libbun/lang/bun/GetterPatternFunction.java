package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZGetterNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class GetterPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode GetterNode = new ZGetterNode(ParentNode, LeftNode);
		GetterNode = TokenContext.MatchToken(GetterNode, ".", ZTokenContext._Required);
		GetterNode = TokenContext.MatchPattern(GetterNode, ZGetterNode._NameInfo, "$Name$", ZTokenContext._Required);
		return GetterNode;
	}

}
