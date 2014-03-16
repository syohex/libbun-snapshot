package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZInstanceOfNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class InstanceOfPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode BinaryNode = new ZInstanceOfNode(ParentNode, TokenContext.GetToken(ZTokenContext._MoveNext), LeftNode);
		BinaryNode = TokenContext.MatchPattern(BinaryNode, ZInstanceOfNode._TypeInfo, "$OpenType$", ZTokenContext._Required);
		return BinaryNode;
	}

}
