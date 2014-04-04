package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZGetIndexNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class GetIndexPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode IndexerNode = new ZGetIndexNode(ParentNode, LeftNode);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "[", ZTokenContext._Required);
		IndexerNode = TokenContext.MatchPattern(IndexerNode, ZGetIndexNode._Index, "$Expression$", ZTokenContext._Required, ZTokenContext._AllowSkipIndent);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "]", ZTokenContext._Required);
		return IndexerNode;
	}

}
