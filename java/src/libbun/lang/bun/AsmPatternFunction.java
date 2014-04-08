package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.literal.BunAsmNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class AsmPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode AsmNode = new BunAsmNode(ParentNode, null, null, null);
		AsmNode = TokenContext.MatchToken(AsmNode, "asm", BTokenContext._Required);
		AsmNode = TokenContext.MatchToken(AsmNode, "(", BTokenContext._Required);
		AsmNode = TokenContext.MatchPattern(AsmNode, BunAsmNode._Macro, "$StringLiteral$", BTokenContext._Required);
		AsmNode = TokenContext.MatchToken(AsmNode, ")", BTokenContext._Required);
		AsmNode = TokenContext.MatchPattern(AsmNode, BunAsmNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		return AsmNode;
	}
}
