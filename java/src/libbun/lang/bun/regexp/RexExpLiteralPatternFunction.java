package libbun.lang.bun.regexp;

import libbun.ast.BNewObjectNode;
import libbun.ast.BNode;
import libbun.ast.BStringNode;
import libbun.ast.ZMethodCallNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class RexExpLiteralPatternFunction extends BMatchFunction {
	// ["pattern", "flag"] => (MethodCall "RegExp_Init" (NewObject "pattern", "flag"))
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var String PatternFlag = "";
		if(TokenContext.MatchToken("$RexExpLiteralFlag$")) {
			PatternFlag = TokenContext.GetToken(BTokenContext._MoveNext).GetText();
		}
		ZMethodCallNode Node = new ZMethodCallNode(ParentNode, new BNewObjectNode(ParentNode));
		Node.GivenName = "RegExp_Init";
		Node.Append(new BStringNode(ParentNode, Token, Token.GetText()));
		Node.Append(new BStringNode(ParentNode, Token, PatternFlag));
		return Node;
	}
}
