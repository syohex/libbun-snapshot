package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BLetVarNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BDefineNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class BunDefinePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LetNode = new BLetVarNode(ParentNode, BLetVarNode._IsReadOnly, null, null);
		LetNode = TokenContext.MatchToken(LetNode, "define", BTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BLetVarNode._NameInfo, "$DefineName$", BTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BLetVarNode._InitValue, "$StringLiteral$", BTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BLetVarNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Required);
		if(LetNode instanceof BLetVarNode) {
			return new BDefineNode(ParentNode, (BLetVarNode)LetNode);
		}
		return LetNode;
	}

}
