package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.binary.BBinaryNode;
import libbun.ast.binary.BAndNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class AndPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BAndNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode, TokenContext.GetApplyingSyntax());
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}

}
