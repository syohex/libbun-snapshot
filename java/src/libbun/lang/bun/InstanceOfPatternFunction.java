package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZInstanceOfNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class InstanceOfPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode BinaryNode = new ZInstanceOfNode(ParentNode, TokenContext.GetToken(ZTokenContext._MoveNext), LeftNode);
		BinaryNode = TokenContext.MatchPattern(BinaryNode, ZInstanceOfNode._TypeInfo, "$OpenType$", ZTokenContext._Required);
		return BinaryNode;
	}

}
