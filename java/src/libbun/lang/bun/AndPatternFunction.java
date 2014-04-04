package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.ZAndNode;
import libbun.parser.ast.ZBinaryNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class AndPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var ZBinaryNode BinaryNode = new ZAndNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode, TokenContext.GetApplyingSyntax());
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}

}
