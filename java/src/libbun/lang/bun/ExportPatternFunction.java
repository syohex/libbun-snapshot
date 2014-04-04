package libbun.lang.bun;

import libbun.ast.BNode;
import libbun.ast.decl.BClassNode;
import libbun.ast.decl.BFunctionNode;
import libbun.ast.decl.BLetVarNode;
import libbun.ast.error.BErrorNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ExportPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken NameToken = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var BNode Node = TokenContext.ParsePattern(ParentNode, "function", BTokenContext._Optional);
		if(Node instanceof BFunctionNode) {
			((BFunctionNode)Node).IsExport = true;
			return Node;
		}
		Node = TokenContext.ParsePattern(ParentNode, "let", BTokenContext._Optional);
		if(Node instanceof BLetVarNode) {
			((BLetVarNode)Node).NameFlag = ((BLetVarNode)Node).NameFlag | BLetVarNode._IsExport;
			return Node;
		}
		Node = TokenContext.ParsePattern(ParentNode, "class", BTokenContext._Optional);
		if(Node instanceof BClassNode) {
			((BClassNode)Node).IsExport = true;
			return Node;
		}

		return new BErrorNode(ParentNode, NameToken, "export function, class, or let");
	}


}
