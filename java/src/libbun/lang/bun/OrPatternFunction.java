package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZBinaryNode;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZOrNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class OrPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZBinaryNode BinaryNode = new ZOrNode(ParentNode, TokenContext.GetToken(ZTokenContext._MoveNext), LeftNode, TokenContext.GetApplyingSyntax());
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}

}
