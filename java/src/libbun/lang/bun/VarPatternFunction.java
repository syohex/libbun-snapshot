package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BLetVarNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZVarBlockNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class VarPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode VarNode = new BLetVarNode(ParentNode, 0, null, null);
		VarNode = TokenContext.MatchToken(VarNode, "var", ZTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, BLetVarNode._NameInfo, "$Name$", ZTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, BLetVarNode._TypeInfo, "$TypeAnnotation$", ZTokenContext._Optional);
		VarNode = TokenContext.MatchToken(VarNode, "=", ZTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, BLetVarNode._InitValue, "$Expression$", ZTokenContext._Required);
		if(VarNode instanceof BLetVarNode) {
			return new ZVarBlockNode(ParentNode, (BLetVarNode)VarNode);
		}
		return VarNode;
	}

}
