package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZBinaryNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZOrNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class OrPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var ZBinaryNode BinaryNode = new ZOrNode(ParentNode, TokenContext.GetToken(ZTokenContext._MoveNext), LeftNode, TokenContext.GetApplyingSyntax());
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}

}
