package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZSetterNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class SetterPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode SetterNode = new ZSetterNode(ParentNode, LeftNode);
		SetterNode = TokenContext.MatchToken(SetterNode, ".", ZTokenContext._Required);
		SetterNode = TokenContext.MatchPattern(SetterNode, ZSetterNode._NameInfo, "$Name$", ZTokenContext._Required);
		SetterNode = TokenContext.MatchToken(SetterNode, "=", ZTokenContext._Required);
		SetterNode = TokenContext.MatchPattern(SetterNode, ZSetterNode._Expr, "$Expression$", ZTokenContext._Required);
		return SetterNode;
	}

}
