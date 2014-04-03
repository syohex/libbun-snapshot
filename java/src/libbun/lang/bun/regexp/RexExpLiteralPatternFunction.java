package libbun.lang.bun.regexp;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZMethodCallNode;
import libbun.parser.ast.ZNewObjectNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BStringNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class RexExpLiteralPatternFunction extends ZMatchFunction {
	// ["pattern", "flag"] => (MethodCall "RegExp_Init" (NewObject "pattern", "flag"))
	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var ZToken Token = TokenContext.GetToken(ZTokenContext._MoveNext);
		@Var String PatternFlag = "";
		if(TokenContext.MatchToken("$RexExpLiteralFlag$")) {
			PatternFlag = TokenContext.GetToken(ZTokenContext._MoveNext).GetText();
		}
		ZMethodCallNode Node = new ZMethodCallNode(ParentNode, new ZNewObjectNode(ParentNode));
		Node.GivenName = "RegExp_Init";
		Node.Append(new BStringNode(ParentNode, Token, Token.GetText()));
		Node.Append(new BStringNode(ParentNode, Token, PatternFlag));
		return Node;
	}
}
