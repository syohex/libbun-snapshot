package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZClassNode;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.ZLetNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class ExportPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZToken NameToken = TokenContext.GetToken(ZTokenContext._MoveNext);
		@Var ZNode Node = TokenContext.ParsePattern(ParentNode, "function", ZTokenContext._Optional);
		if(Node instanceof ZFunctionNode) {
			((ZFunctionNode)Node).IsExport = true;
			return Node;
		}
		Node = TokenContext.ParsePattern(ParentNode, "let", ZTokenContext._Optional);
		if(Node instanceof ZLetNode) {
			((ZLetNode)Node).IsExport = true;
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
