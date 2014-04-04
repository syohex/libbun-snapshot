package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.ZInstanceOfNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class InstanceOfPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode BinaryNode = new ZInstanceOfNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		BinaryNode = TokenContext.MatchPattern(BinaryNode, ZInstanceOfNode._TypeInfo, "$OpenType$", BTokenContext._Required);
		return BinaryNode;
	}

}
