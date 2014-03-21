package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZLetVarNode;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZVarBlockNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class VarPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode VarNode = new ZLetVarNode(ParentNode, 0);
		VarNode = TokenContext.MatchToken(VarNode, "var", ZTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, ZLetVarNode._NameInfo, "$Name$", ZTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, ZLetVarNode._TypeInfo, "$TypeAnnotation$", ZTokenContext._Optional);
		VarNode = TokenContext.MatchToken(VarNode, "=", ZTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, ZLetVarNode._InitValue, "$Expression$", ZTokenContext._Required);
		if(VarNode instanceof ZLetVarNode) {
			return new ZVarBlockNode(ParentNode, (ZLetVarNode)VarNode);
		}
		return VarNode;
	}

}
