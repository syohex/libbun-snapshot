package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.expression.BGetIndexNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class GetIndexPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode IndexerNode = new BGetIndexNode(ParentNode, LeftNode);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "[", BTokenContext._Required);
		IndexerNode = TokenContext.MatchPattern(IndexerNode, BGetIndexNode._Index, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "]", BTokenContext._Required);
		return IndexerNode;
	}

}
