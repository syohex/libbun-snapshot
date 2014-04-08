package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.decl.BunDefineNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class BunDefinePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LetNode = new BunLetVarNode(ParentNode, BunLetVarNode._IsReadOnly, null, null);
		LetNode = TokenContext.MatchToken(LetNode, "define", BTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BunLetVarNode._NameInfo, "$DefineName$", BTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BunLetVarNode._InitValue, "$StringLiteral$", BTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BunLetVarNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Required);
		if(LetNode instanceof BunLetVarNode) {
			return new BunDefineNode(ParentNode, (BunLetVarNode)LetNode);
		}
		return LetNode;
	}

}
