package libbun.lang.bun;

import libbun.ast.BLetVarNode;
import libbun.ast.BNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class LetPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LetNode = new BLetVarNode(ParentNode, BLetVarNode._IsReadOnly, null, null);
		LetNode = TokenContext.MatchToken(LetNode, "let", BTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BLetVarNode._NameInfo, "$Name$", BTokenContext._Required);
		//		if(TokenContext.MatchToken(".")) {
		//			LetNode = TokenContext.MatchPattern(LetNode, "$Name$", ZTokenContext.Required);
		//		}
		LetNode = TokenContext.MatchPattern(LetNode, BLetVarNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		LetNode = TokenContext.MatchToken(LetNode, "=", BTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BLetVarNode._InitValue, "$Expression$", BTokenContext._Required);
		return LetNode;
	}

}
