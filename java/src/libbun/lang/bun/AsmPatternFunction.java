package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BAsmNode;
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
