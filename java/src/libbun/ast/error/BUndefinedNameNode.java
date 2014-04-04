package libbun.ast.error;

import libbun.ast.expression.BGetNameNode;
import libbun.util.BField;

public class BUndefinedNameNode extends BErrorNode {
	@BField BGetNameNode ErrorNode;
	public BUndefinedNameNode(BGetNameNode ErrorNode) {
		super(ErrorNode.ParentNode, ErrorNode.SourceToken, "undefined name: " + ErrorNode.GivenName);
		this.ErrorNode = ErrorNode;
	}

}
