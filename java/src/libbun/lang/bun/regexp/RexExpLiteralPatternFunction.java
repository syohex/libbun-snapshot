package libbun.lang.bun.regexp;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZMethodCallNode;
import libbun.parser.ast.ZNewObjectNode;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZStringNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class RexExpLiteralPatternFunction extends ZMatchFunction {
	// ["pattern", "flag"] => (MethodCall "RegExp_Init" (NewObject "pattern", "flag"))
	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZToken Token = TokenContext.GetToken(ZTokenContext._MoveNext);
		@Var String PatternFlag = "";
		if(TokenContext.MatchToken("$RexExpLiteralFlag$")) {
			PatternFlag = TokenContext.GetToken(ZTokenContext._MoveNext).GetText();
		}
		ZMethodCallNode Node = new ZMethodCallNode(ParentNode, new ZNewObjectNode(ParentNode));
		Node.GivenName = "RegExp_Init";
		Node.Append(new ZStringNode(ParentNode, Token, Token.GetText()));
		Node.Append(new ZStringNode(ParentNode, Token, PatternFlag));
		return Node;
	}
}
