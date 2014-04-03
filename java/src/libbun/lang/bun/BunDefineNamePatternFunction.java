package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.BGetNameNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class BunDefineNamePatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var ZToken NameToken = TokenContext.ParseLargeToken();
		//System.out.println("'"+ NameToken.GetText() + "'");
		return new BGetNameNode(ParentNode, NameToken, NameToken.GetText());
	}

}
