package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.binary.BBinaryNode;
import libbun.ast.binary.BOrNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class OrPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BOrNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode, TokenContext.GetApplyingSyntax());
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}

}
