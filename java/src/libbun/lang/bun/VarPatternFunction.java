package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.ast.decl.ZVarBlockNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class VarPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode VarNode = new BunLetVarNode(ParentNode, 0, null, null);
		VarNode = TokenContext.MatchToken(VarNode, "var", BTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, BunLetVarNode._NameInfo, "$Name$", BTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, BunLetVarNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		VarNode = TokenContext.MatchToken(VarNode, "=", BTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, BunLetVarNode._InitValue, "$Expression$", BTokenContext._Required);
		if(VarNode instanceof BunLetVarNode) {
			return new ZVarBlockNode(ParentNode, (BunLetVarNode)VarNode);
		}
		return VarNode;
	}

}
