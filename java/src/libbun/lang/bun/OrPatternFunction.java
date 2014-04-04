package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZBinaryNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZOrNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class OrPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var ZBinaryNode BinaryNode = new ZOrNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode, TokenContext.GetApplyingSyntax());
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}

}
