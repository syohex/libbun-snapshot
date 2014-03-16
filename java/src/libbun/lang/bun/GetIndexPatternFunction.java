package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZGetIndexNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class GetIndexPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode IndexerNode = new ZGetIndexNode(ParentNode, LeftNode);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "[", ZTokenContext._Required);
		IndexerNode = TokenContext.MatchPattern(IndexerNode, ZGetIndexNode._Index, "$Expression$", ZTokenContext._Required, ZTokenContext._AllowSkipIndent);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "]", ZTokenContext._Required);
		return IndexerNode;
	}

}
