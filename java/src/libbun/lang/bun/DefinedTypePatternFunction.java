package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.BTypeNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.type.BType;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class DefinedTypePatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var BType Type = ParentNode.GetNameSpace().GetType(Token.GetText(), Token, false/*IsCreation*/);
		if(Type != null) {
			@Var BTypeNode TypeNode = new BTypeNode(ParentNode, Token, Type);
			return TokenContext.ParsePatternAfter(ParentNode, TypeNode, "$TypeRight$", BTokenContext._Optional);
		}
		return null;
	}
}
