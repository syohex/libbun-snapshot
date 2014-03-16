package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZAsmMacroNode;
import libbun.parser.ast.ZAsmNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class AsmPatternFunction extends ZMatchFunction {
	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode AsmNode = new ZAsmNode(ParentNode);
		AsmNode = TokenContext.MatchToken(AsmNode, "asm", ZTokenContext._Required);
		AsmNode = TokenContext.MatchToken(AsmNode, "(", ZTokenContext._Required);
		AsmNode = TokenContext.MatchPattern(AsmNode, ZAsmMacroNode._Macro, "$StringLiteral$", ZTokenContext._Required);
		AsmNode = TokenContext.MatchToken(AsmNode, ")", ZTokenContext._Required);
		AsmNode = TokenContext.MatchPattern(AsmNode, ZAsmNode._TypeInfo, "$TypeAnnotation$", ZTokenContext._Optional);
		return AsmNode;
	}
}
