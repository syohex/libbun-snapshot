package libbun.lang.bun;

import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZTypeNode;
import libbun.type.BType;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class DefinedTypePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var BType Type = ParentNode.GetNameSpace().GetType(Token.GetText(), Token, false/*IsCreation*/);
		if(Type != null) {
			@Var ZTypeNode TypeNode = new ZTypeNode(ParentNode, Token, Type);
			return TokenContext.ParsePatternAfter(ParentNode, TypeNode, "$TypeRight$", BTokenContext._Optional);
		}
		return null;
	}
}
