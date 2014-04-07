package libbun.lang.python;

import libbun.ast.BNode;
import libbun.ast.statement.BWhileNode;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;
import libbun.util.Var;

public class PythonWhilePatternFunction extends BMatchFunction {

	@Override
	public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode WhileNode = new BWhileNode(ParentNode);
		WhileNode = TokenContext.MatchToken(WhileNode, "while", BTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, BWhileNode._Cond, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		WhileNode = TokenContext.MatchPattern(WhileNode, BWhileNode._Block, "$Block$", BTokenContext._Required);
		return WhileNode;
	}

}
