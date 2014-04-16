package libbun.lang.bun.regexp;

import libbun.ast.BNode;
import libbun.ast.expression.MethodCallNode;
import libbun.ast.expression.NewObjectNode;
import libbun.ast.literal.BunStringNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;
import libbun.util.Var;

public class RexExpLiteralPatternFunction extends BMatchFunction {
	// ["pattern", "flag"] => (MethodCall "RegExp_Init" (NewObject "pattern", "flag"))
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var String PatternFlag = "";
		if(TokenContext.MatchToken("$RexExpLiteralFlag$")) {
			PatternFlag = TokenContext.GetToken(BTokenContext._MoveNext).GetText();
		}
		MethodCallNode Node = new MethodCallNode(ParentNode, new NewObjectNode(ParentNode), "RegExp_Init");
		Node.Append(new BunStringNode(ParentNode, Token, Token.GetText()));
		Node.Append(new BunStringNode(ParentNode, Token, PatternFlag));
		return Node;
	}
}
