package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.literal.BTypeNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.parser.BTypeChecker;
import libbun.type.BType;
import libbun.type.BTypePool;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class OpenTypePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken MaybeToken   = null;
		@Var BToken MutableToken = null;
		if(TokenContext.IsToken("maybe")) {
			MaybeToken   = TokenContext.GetToken(BTokenContext._MoveNext);
		}
		if(TokenContext.MatchToken("mutable")) {
			MutableToken   = TokenContext.GetToken(BTokenContext._MoveNext);
		}
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var BType Type = ParentNode.GetNameSpace().GetType(Token.GetText(), Token, true/*IsCreation*/);
		if(Type != null) {
			@Var BTypeNode TypeNode = new BTypeNode(ParentNode, Token, Type);
			@Var BNode Node = TokenContext.ParsePatternAfter(ParentNode, TypeNode, "$TypeRight$", BTokenContext._Optional);
			if(Node instanceof BTypeNode) {
				@Var BTypeChecker Gamma = ParentNode.GetNameSpace().Generator.TypeChecker;
				if(MutableToken != null) {
					Node.Type = BTypePool._LookupMutableType(Gamma, Node.Type, MutableToken);
				}
				if(MaybeToken != null) {
					Node.Type = BTypePool._LookupNullableType(Gamma, Node.Type, MaybeToken);
				}
			}
			return Node;
		}
		return null; // Not Matched
	}

}
