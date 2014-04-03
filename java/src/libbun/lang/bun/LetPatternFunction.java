package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BLetVarNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class LetPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LetNode = new BLetVarNode(ParentNode, BLetVarNode._IsReadOnly, null, null);
		LetNode = TokenContext.MatchToken(LetNode, "let", ZTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BLetVarNode._NameInfo, "$Name$", ZTokenContext._Required);
		//		if(TokenContext.MatchToken(".")) {
		//			LetNode = TokenContext.MatchPattern(LetNode, "$Name$", ZTokenContext.Required);
		//		}
		LetNode = TokenContext.MatchPattern(LetNode, BLetVarNode._TypeInfo, "$TypeAnnotation$", ZTokenContext._Optional);
		LetNode = TokenContext.MatchToken(LetNode, "=", ZTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BLetVarNode._InitValue, "$Expression$", ZTokenContext._Required);
		return LetNode;
	}

}
