package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZNewObjectNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class NewObjectPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LiteralNode = new ZNewObjectNode(ParentNode);
		LiteralNode = TokenContext.MatchToken(LiteralNode, "new", BTokenContext._Required);
		LiteralNode = TokenContext.MatchPattern(LiteralNode, ZNewObjectNode._TypeInfo, "$OpenType$", BTokenContext._Optional);
		LiteralNode = TokenContext.MatchNtimes(LiteralNode, "(", "$Expression$", ",", ")");
		return LiteralNode;
	}

}
