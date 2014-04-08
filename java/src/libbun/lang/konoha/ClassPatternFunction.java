package libbun.lang.konoha;

import libbun.ast.BNode;
import libbun.ast.decl.BClassNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ClassPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ClassNode = new BClassNode(ParentNode);
		ClassNode = TokenContext.MatchToken(ClassNode, "class", BTokenContext._Required);
		ClassNode = TokenContext.MatchPattern(ClassNode, BClassNode._NameInfo, "$Name$", BTokenContext._Required);
		if(TokenContext.MatchNewLineToken("extends")) {
			ClassNode = TokenContext.MatchPattern(ClassNode, BClassNode._TypeInfo, "$OpenType$", BTokenContext._Required);
		}
		ClassNode = TokenContext.MatchNtimes(ClassNode, "{", "$ClassMember$", null, "}");
		return ClassNode;
	}

}
