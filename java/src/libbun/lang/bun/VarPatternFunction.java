package libbun.lang.bun;

import libbun.ast.BLetVarNode;
import libbun.ast.BNode;
import libbun.ast.ZVarBlockNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class VarPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode VarNode = new BLetVarNode(ParentNode, 0, null, null);
		VarNode = TokenContext.MatchToken(VarNode, "var", BTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, BLetVarNode._NameInfo, "$Name$", BTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, BLetVarNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		VarNode = TokenContext.MatchToken(VarNode, "=", BTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, BLetVarNode._InitValue, "$Expression$", BTokenContext._Required);
		if(VarNode instanceof BLetVarNode) {
			return new ZVarBlockNode(ParentNode, (BLetVarNode)VarNode);
		}
		return VarNode;
	}

}
