package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.expression.GetNameNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class BunDefineNamePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken NameToken = TokenContext.ParseLargeToken();
		//System.out.println("'"+ NameToken.GetText() + "'");
		return new GetNameNode(ParentNode, NameToken, NameToken.GetText());
	}

}
