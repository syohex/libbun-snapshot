package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZGetNameNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class BunDefineNamePatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZToken NameToken = TokenContext.ParseLargeToken();
		//System.out.println("'"+ NameToken.GetText() + "'");
		return new ZGetNameNode(ParentNode, NameToken, NameToken.GetText());
	}

}
