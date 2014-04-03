package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ZTypeChecker;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZTypeNode;
import libbun.type.ZType;
import libbun.type.ZTypePool;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class OpenTypePatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var ZToken MaybeToken   = null;
		@Var ZToken MutableToken = null;
		if(TokenContext.IsToken("maybe")) {
			MaybeToken   = TokenContext.GetToken(ZTokenContext._MoveNext);
		}
		if(TokenContext.MatchToken("mutable")) {
			MutableToken   = TokenContext.GetToken(ZTokenContext._MoveNext);
		}
		@Var ZToken Token = TokenContext.GetToken(ZTokenContext._MoveNext);
		@Var ZType Type = ParentNode.GetNameSpace().GetType(Token.GetText(), Token, true/*IsCreation*/);
		if(Type != null) {
			@Var ZTypeNode TypeNode = new ZTypeNode(ParentNode, Token, Type);
			@Var BNode Node = TokenContext.ParsePatternAfter(ParentNode, TypeNode, "$TypeRight$", ZTokenContext._Optional);
			if(Node instanceof ZTypeNode) {
				@Var ZTypeChecker Gamma = ParentNode.GetNameSpace().Generator.TypeChecker;
				if(MutableToken != null) {
					Node.Type = ZTypePool._LookupMutableType(Gamma, Node.Type, MutableToken);
				}
				if(MaybeToken != null) {
					Node.Type = ZTypePool._LookupNullableType(Gamma, Node.Type, MaybeToken);
				}
			}
			return Node;
		}
		return null; // Not Matched
	}

}
