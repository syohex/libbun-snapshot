package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZIfNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class IfPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode IfNode = new ZIfNode(ParentNode);
		IfNode = TokenContext.MatchToken(IfNode, "if", ZTokenContext._Required);
		IfNode = TokenContext.MatchToken(IfNode, "(", ZTokenContext._Required);
		IfNode = TokenContext.MatchPattern(IfNode, ZIfNode._Cond, "$Expression$", ZTokenContext._Required, ZTokenContext._AllowNewLine);
		IfNode = TokenContext.MatchToken(IfNode, ")", ZTokenContext._Required);
		IfNode = TokenContext.MatchPattern(IfNode, ZIfNode._Then, "$Block$", ZTokenContext._Required);
		if(TokenContext.MatchNewLineToken("else")) {
			if(TokenContext.IsNewLineToken("if")) {
				IfNode = TokenContext.MatchPattern(IfNode, ZIfNode._Else, "if", ZTokenContext._Required);
			}
			else {
				IfNode = TokenContext.MatchPattern(IfNode, ZIfNode._Else, "$Block$", ZTokenContext._Required);
			}
		}
		return IfNode;
	}

}
