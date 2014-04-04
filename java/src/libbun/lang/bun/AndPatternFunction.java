package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.binary.BAndNode;
import libbun.ast.binary.BBinaryNode;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;
import libbun.util.Var;

public class AndPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BAndNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode, BunPrecedence._CStyleAND);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}

}
