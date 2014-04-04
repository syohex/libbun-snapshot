package libbun.encode.jvm;

import libbun.ast.BNode;
import libbun.ast.expression.BGetNameNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class JavaClassPathPattern extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.ParseLargeToken();
		return new BGetNameNode(ParentNode, Token, Token.GetText());
	}

}
