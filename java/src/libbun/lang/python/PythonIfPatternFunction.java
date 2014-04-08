package libbun.lang.python;

import libbun.ast.BNode;
import libbun.ast.statement.BunIfNode;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;
import libbun.util.Var;

public class PythonIfPatternFunction extends BMatchFunction{
	@Override
	public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode IfNode = new BunIfNode(ParentNode);
		IfNode = TokenContext.MatchToken(IfNode, "if", BTokenContext._Required);
		IfNode = TokenContext.MatchPattern(IfNode, BunIfNode._Cond, "$Expression$", BTokenContext._Required, BTokenContext._AllowNewLine);
		IfNode = TokenContext.MatchPattern(IfNode, BunIfNode._Then, "$Block$", BTokenContext._Required);
		//FIXME elif
		if(TokenContext.MatchNewLineToken("else")) {
			IfNode = TokenContext.MatchPattern(IfNode, BunIfNode._Else, "$Block$", BTokenContext._Required);
		}
		return IfNode;
	}
}
