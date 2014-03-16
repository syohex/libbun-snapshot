package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZGetterNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class GetterPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode GetterNode = new ZGetterNode(ParentNode, LeftNode);
		GetterNode = TokenContext.MatchToken(GetterNode, ".", ZTokenContext._Required);
		GetterNode = TokenContext.MatchPattern(GetterNode, ZGetterNode._NameInfo, "$Name$", ZTokenContext._Required);
		return GetterNode;
	}

}
