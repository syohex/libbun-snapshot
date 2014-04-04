package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZSetterNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class SetterPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode SetterNode = new ZSetterNode(ParentNode, LeftNode);
		SetterNode = TokenContext.MatchToken(SetterNode, ".", BTokenContext._Required);
		SetterNode = TokenContext.MatchPattern(SetterNode, ZSetterNode._NameInfo, "$Name$", BTokenContext._Required);
		SetterNode = TokenContext.MatchToken(SetterNode, "=", BTokenContext._Required);
		SetterNode = TokenContext.MatchPattern(SetterNode, ZSetterNode._Expr, "$Expression$", BTokenContext._Required);
		return SetterNode;
	}

}
