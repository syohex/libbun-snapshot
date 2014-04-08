package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.expression.SetIndexNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class SetIndexPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode IndexerNode = new SetIndexNode(ParentNode, LeftNode);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "[", BTokenContext._Required);
		IndexerNode = TokenContext.MatchPattern(IndexerNode, SetIndexNode._Index, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "]", BTokenContext._Required);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "=", BTokenContext._Required);
		IndexerNode = TokenContext.MatchPattern(IndexerNode, SetIndexNode._Expr, "$Expression$", BTokenContext._Required);
		return IndexerNode;
	}

}
