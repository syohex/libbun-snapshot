package libbun.ast;

import libbun.encode.jvm.JavaImportNode;
import libbun.parser.BNameSpace;
import libbun.util.Var;

public class BRequireNode extends ZTopLevelNode {
	public final static int _Path = 0;

	public BRequireNode(BNode ParentNode) {
		super(ParentNode, null, 1);
	}

	@Override public final void Perform(BNameSpace NameSpace) {
		@Var String ResourcePath = this.AST[JavaImportNode._Path].SourceToken.GetTextAsName();
		NameSpace.Generator.RequireLibrary(ResourcePath, this.GetAstToken(JavaImportNode._Path));
	}

}
