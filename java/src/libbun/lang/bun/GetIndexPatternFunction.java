package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZGetIndexNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class GetIndexPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode IndexerNode = new ZGetIndexNode(ParentNode, LeftNode);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "[", BTokenContext._Required);
		IndexerNode = TokenContext.MatchPattern(IndexerNode, ZGetIndexNode._Index, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "]", BTokenContext._Required);
		return IndexerNode;
	}

}
