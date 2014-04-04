package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZClassNode;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.BLetVarNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ExportPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var ZToken NameToken = TokenContext.GetToken(ZTokenContext._MoveNext);
		@Var BNode Node = TokenContext.ParsePattern(ParentNode, "function", ZTokenContext._Optional);
		if(Node instanceof ZFunctionNode) {
			((ZFunctionNode)Node).IsExport = true;
			return Node;
		}
		Node = TokenContext.ParsePattern(ParentNode, "let", ZTokenContext._Optional);
		if(Node instanceof BLetVarNode) {
			((BLetVarNode)Node).NameFlag = ((BLetVarNode)Node).NameFlag | BLetVarNode._IsExport;
			return Node;
		}
		Node = TokenContext.ParsePattern(ParentNode, "class", ZTokenContext._Optional);
		if(Node instanceof ZClassNode) {
			((ZClassNode)Node).IsExport = true;
			return Node;
		}

		return new ZErrorNode(ParentNode, NameToken, "export function, class, or let");
	}


}
