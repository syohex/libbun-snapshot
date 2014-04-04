package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.expression.BNewObjectNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class NewObjectPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LiteralNode = new BNewObjectNode(ParentNode);
		LiteralNode = TokenContext.MatchToken(LiteralNode, "new", BTokenContext._Required);
		LiteralNode = TokenContext.MatchPattern(LiteralNode, BNewObjectNode._TypeInfo, "$OpenType$", BTokenContext._Optional);
		LiteralNode = TokenContext.MatchNtimes(LiteralNode, "(", "$Expression$", ",", ")");
		return LiteralNode;
	}

}
