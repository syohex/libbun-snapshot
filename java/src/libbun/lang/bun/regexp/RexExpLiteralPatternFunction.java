package libbun.lang.bun.regexp;

import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.parser.ast.ZMethodCallNode;
import libbun.parser.ast.ZNewObjectNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BStringNode;
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
		ZMethodCallNode Node = new ZMethodCallNode(ParentNode, new ZNewObjectNode(ParentNode));
		Node.GivenName = "RegExp_Init";
		Node.Append(new BStringNode(ParentNode, Token, Token.GetText()));
		Node.Append(new BStringNode(ParentNode, Token, PatternFlag));
		return Node;
	}
}
