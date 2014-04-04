package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.binary.BBinaryNode;
import libbun.ast.binary.BOrNode;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;
import libbun.util.Var;

public class OrPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BOrNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode, BunPrecedence._CStyleOR);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}

}
