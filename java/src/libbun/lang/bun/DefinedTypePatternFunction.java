package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZTypeNode;
import libbun.type.ZType;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class DefinedTypePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var ZToken Token = TokenContext.GetToken(ZTokenContext._MoveNext);
		@Var ZType Type = ParentNode.GetNameSpace().GetType(Token.GetText(), Token, false/*IsCreation*/);
		if(Type != null) {
			@Var ZTypeNode TypeNode = new ZTypeNode(ParentNode, Token, Type);
			return TokenContext.ParsePatternAfter(ParentNode, TypeNode, "$TypeRight$", ZTokenContext._Optional);
		}
		return null;
	}
}
