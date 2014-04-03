package libbun.encode.jvm;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class JavaImportPattern extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ImportNode = new JavaImportNode(ParentNode);
		ImportNode = TokenContext.MatchToken(ImportNode, "import", ZTokenContext._Required);
		ImportNode = TokenContext.MatchPattern(ImportNode, JavaImportNode._Path, "$JavaClassPath$", ZTokenContext._Required);
		return ImportNode;
	}

}
