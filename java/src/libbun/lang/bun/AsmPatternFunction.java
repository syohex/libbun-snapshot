package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BAsmNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class AsmPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode AsmNode = new BAsmNode(ParentNode, null, null, null);
		AsmNode = TokenContext.MatchToken(AsmNode, "asm", ZTokenContext._Required);
		AsmNode = TokenContext.MatchToken(AsmNode, "(", ZTokenContext._Required);
		AsmNode = TokenContext.MatchPattern(AsmNode, BAsmNode._Macro, "$StringLiteral$", ZTokenContext._Required);
		AsmNode = TokenContext.MatchToken(AsmNode, ")", ZTokenContext._Required);
		AsmNode = TokenContext.MatchPattern(AsmNode, BAsmNode._TypeInfo, "$TypeAnnotation$", ZTokenContext._Optional);
		return AsmNode;
	}
}
