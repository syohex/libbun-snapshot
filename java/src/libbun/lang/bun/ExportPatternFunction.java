package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.decl.BunClassNode;
import libbun.ast.decl.BunFunctionNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.ast.error.ErrorNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ExportPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken NameToken = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var BNode Node = TokenContext.ParsePattern(ParentNode, "function", BTokenContext._Optional);
		if(Node instanceof BunFunctionNode) {
			((BunFunctionNode)Node).IsExport = true;
			return Node;
		}
		Node = TokenContext.ParsePattern(ParentNode, "let", BTokenContext._Optional);
		if(Node instanceof BunLetVarNode) {
			((BunLetVarNode)Node).NameFlag = ((BunLetVarNode)Node).NameFlag | BunLetVarNode._IsExport;
			return Node;
		}
		Node = TokenContext.ParsePattern(ParentNode, "class", BTokenContext._Optional);
		if(Node instanceof BunClassNode) {
			((BunClassNode)Node).IsExport = true;
			return Node;
		}

		return new ErrorNode(ParentNode, NameToken, "export function, class, or let");
	}


}
