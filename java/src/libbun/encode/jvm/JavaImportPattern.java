package libbun.encode.jvm;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class JavaImportPattern extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var ZNode ImportNode = new JavaImportNode(ParentNode);
		ImportNode = TokenContext.MatchToken(ImportNode, "import", ZTokenContext._Required);
		ImportNode = TokenContext.MatchPattern(ImportNode, JavaImportNode._Path, "$JavaClassPath$", ZTokenContext._Required);
		return ImportNode;
	}

}
