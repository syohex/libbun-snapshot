package libbun.lang.bun;

import libbun.ast.BAsmNode;
import libbun.ast.BNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class AsmPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode AsmNode = new BAsmNode(ParentNode, null, null, null);
		AsmNode = TokenContext.MatchToken(AsmNode, "asm", BTokenContext._Required);
		AsmNode = TokenContext.MatchToken(AsmNode, "(", BTokenContext._Required);
		AsmNode = TokenContext.MatchPattern(AsmNode, BAsmNode._Macro, "$StringLiteral$", BTokenContext._Required);
		AsmNode = TokenContext.MatchToken(AsmNode, ")", BTokenContext._Required);
		AsmNode = TokenContext.MatchPattern(AsmNode, BAsmNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		return AsmNode;
	}
}
