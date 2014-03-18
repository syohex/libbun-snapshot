package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZLetVarNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class FieldPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var boolean Rememberd = TokenContext.SetParseFlag(false);
		@Var ZNode FieldNode = new ZLetVarNode(ParentNode, false);
		FieldNode = TokenContext.MatchToken(FieldNode, "var", ZTokenContext._Required);
		FieldNode = TokenContext.MatchPattern(FieldNode, ZLetVarNode._NameInfo, "$Name$", ZTokenContext._Required);
		FieldNode = TokenContext.MatchPattern(FieldNode, ZLetVarNode._TypeInfo, "$TypeAnnotation$", ZTokenContext._Optional);
		if(TokenContext.MatchToken("=")) {
			FieldNode = TokenContext.MatchPattern(FieldNode, ZLetVarNode._InitValue, "$Expression$", ZTokenContext._Required);
		}
		FieldNode = TokenContext.MatchPattern(FieldNode, ZNode._Nop, ";", ZTokenContext._Required);
		TokenContext.SetParseFlag(Rememberd);
		return FieldNode;
	}

}
