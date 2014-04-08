package libbun.ast.error;

import libbun.ast.expression.GetNameNode;
import libbun.util.BField;

public class BReadOnlyNameNode extends ErrorNode {
	@BField GetNameNode ErrorNode;
	public BReadOnlyNameNode(GetNameNode ErrorNode) {
		super(ErrorNode.ParentNode, ErrorNode.SourceToken, "undefined name: " + ErrorNode.GivenName);
		this.ErrorNode = ErrorNode;
	}

}
