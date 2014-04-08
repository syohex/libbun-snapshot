package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.statement.BunIfNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class IfPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode IfNode = new BunIfNode(ParentNode);
		IfNode = TokenContext.MatchToken(IfNode, "if", BTokenContext._Required);
		IfNode = TokenContext.MatchToken(IfNode, "(", BTokenContext._Required);
		IfNode = TokenContext.MatchPattern(IfNode, BunIfNode._Cond, "$Expression$", BTokenContext._Required, BTokenContext._AllowNewLine);
		IfNode = TokenContext.MatchToken(IfNode, ")", BTokenContext._Required);
		IfNode = TokenContext.MatchPattern(IfNode, BunIfNode._Then, "$Block$", BTokenContext._Required);
		if(TokenContext.MatchNewLineToken("else")) {
			if(TokenContext.IsNewLineToken("if")) {
				IfNode = TokenContext.MatchPattern(IfNode, BunIfNode._Else, "if", BTokenContext._Required);
			}
			else {
				IfNode = TokenContext.MatchPattern(IfNode, BunIfNode._Else, "$Block$", BTokenContext._Required);
			}
		}
		return IfNode;
	}

}
