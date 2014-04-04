package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZSetIndexNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class SetIndexPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode IndexerNode = new ZSetIndexNode(ParentNode, LeftNode);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "[", ZTokenContext._Required);
		IndexerNode = TokenContext.MatchPattern(IndexerNode, ZSetIndexNode._Index, "$Expression$", ZTokenContext._Required, ZTokenContext._AllowSkipIndent);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "]", ZTokenContext._Required);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "=", ZTokenContext._Required);
		IndexerNode = TokenContext.MatchPattern(IndexerNode, ZSetIndexNode._Expr, "$Expression$", ZTokenContext._Required);
		return IndexerNode;
	}

}
