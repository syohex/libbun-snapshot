package libbun.ast.error;

import libbun.ast.expression.BGetNameNode;
import libbun.util.BField;

public class BReadOnlyNameNode extends BErrorNode {
	@BField BGetNameNode ErrorNode;
	public BReadOnlyNameNode(BGetNameNode ErrorNode) {
		super(ErrorNode.ParentNode, ErrorNode.SourceToken, "undefined name: " + ErrorNode.GivenName);
		this.ErrorNode = ErrorNode;
	}

}
