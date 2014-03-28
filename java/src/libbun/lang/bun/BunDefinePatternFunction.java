package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BunDefineNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class BunDefinePatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode AsmNode = new BunDefineNode(ParentNode);
		AsmNode = TokenContext.MatchToken(AsmNode, "define", ZTokenContext._Required);
		AsmNode = TokenContext.MatchPattern(AsmNode, BunDefineNode._NameInfo, "$DefineName$", ZTokenContext._Required);
		AsmNode = TokenContext.MatchPattern(AsmNode, BunDefineNode._Macro, "$StringLiteral$", ZTokenContext._Required);
		AsmNode = TokenContext.MatchPattern(AsmNode, BunDefineNode._TypeInfo, "$TypeAnnotation$", ZTokenContext._Required);
		return AsmNode;
	}

}
