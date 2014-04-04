package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BLetVarNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class FieldPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var boolean Rememberd = TokenContext.SetParseFlag(false);
		@Var BNode FieldNode = new BLetVarNode(ParentNode, 0, null, null);
		FieldNode = TokenContext.MatchToken(FieldNode, "var", BTokenContext._Required);
		FieldNode = TokenContext.MatchPattern(FieldNode, BLetVarNode._NameInfo, "$Name$", BTokenContext._Required);
		FieldNode = TokenContext.MatchPattern(FieldNode, BLetVarNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		if(TokenContext.MatchToken("=")) {
			FieldNode = TokenContext.MatchPattern(FieldNode, BLetVarNode._InitValue, "$Expression$", BTokenContext._Required);
		}
		FieldNode = TokenContext.MatchPattern(FieldNode, BNode._Nop, ";", BTokenContext._Required);
		TokenContext.SetParseFlag(Rememberd);
		return FieldNode;
	}

}
