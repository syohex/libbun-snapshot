package libbun.lang.python;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZIfNode;
import libbun.util.BMatchFunction;
import libbun.util.Var;

public class PythonIfPatternFunction extends BMatchFunction{
	@Override
	public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode IfNode = new ZIfNode(ParentNode);
		IfNode = TokenContext.MatchToken(IfNode, "if", BTokenContext._Required);
		IfNode = TokenContext.MatchPattern(IfNode, ZIfNode._Cond, "$Expression$", BTokenContext._Required, BTokenContext._AllowNewLine);
		IfNode = TokenContext.MatchPattern(IfNode, ZIfNode._Then, "$Block$", BTokenContext._Required);
		//FIXME elif
		if(TokenContext.MatchNewLineToken("else")) {
			IfNode = TokenContext.MatchPattern(IfNode, ZIfNode._Else, "$Block$", BTokenContext._Required);
		}
		return IfNode;
	}
}
