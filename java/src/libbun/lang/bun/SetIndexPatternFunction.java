package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZSetIndexNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class SetIndexPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode IndexerNode = new ZSetIndexNode(ParentNode, LeftNode);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "[", BTokenContext._Required);
		IndexerNode = TokenContext.MatchPattern(IndexerNode, ZSetIndexNode._Index, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "]", BTokenContext._Required);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "=", BTokenContext._Required);
		IndexerNode = TokenContext.MatchPattern(IndexerNode, ZSetIndexNode._Expr, "$Expression$", BTokenContext._Required);
		return IndexerNode;
	}

}
