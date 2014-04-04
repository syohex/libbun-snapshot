package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZBinaryNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class BinaryPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var ZToken Token = TokenContext.GetToken(ZTokenContext._MoveNext);
		@Var ZBinaryNode BinaryNode = new ZBinaryNode(ParentNode, Token, LeftNode, TokenContext.GetApplyingSyntax());
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}

}
