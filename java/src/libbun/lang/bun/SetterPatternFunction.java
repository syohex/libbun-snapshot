package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZSetterNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class SetterPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode SetterNode = new ZSetterNode(ParentNode, LeftNode);
		SetterNode = TokenContext.MatchToken(SetterNode, ".", ZTokenContext._Required);
		SetterNode = TokenContext.MatchPattern(SetterNode, ZSetterNode._NameInfo, "$Name$", ZTokenContext._Required);
		SetterNode = TokenContext.MatchToken(SetterNode, "=", ZTokenContext._Required);
		SetterNode = TokenContext.MatchPattern(SetterNode, ZSetterNode._Expr, "$Expression$", ZTokenContext._Required);
		return SetterNode;
	}

}
