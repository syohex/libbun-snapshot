package libbun.lang.bun;

import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.parser.ast.ZClassNode;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.BLetVarNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ExportPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken NameToken = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var BNode Node = TokenContext.ParsePattern(ParentNode, "function", BTokenContext._Optional);
		if(Node instanceof ZFunctionNode) {
			((ZFunctionNode)Node).IsExport = true;
			return Node;
		}
		Node = TokenContext.ParsePattern(ParentNode, "let", BTokenContext._Optional);
		if(Node instanceof BLetVarNode) {
			((BLetVarNode)Node).NameFlag = ((BLetVarNode)Node).NameFlag | BLetVarNode._IsExport;
			return Node;
		}
		Node = TokenContext.ParsePattern(ParentNode, "class", BTokenContext._Optional);
		if(Node instanceof ZClassNode) {
			((ZClassNode)Node).IsExport = true;
			return Node;
		}

		return new ZErrorNode(ParentNode, NameToken, "export function, class, or let");
	}


}
