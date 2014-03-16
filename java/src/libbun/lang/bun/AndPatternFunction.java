package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZAndNode;
import libbun.parser.ast.ZBinaryNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class AndPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZBinaryNode BinaryNode = new ZAndNode(ParentNode, TokenContext.GetToken(ZTokenContext._MoveNext), LeftNode, TokenContext.GetApplyingSyntax());
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}

}
