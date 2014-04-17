package libbun.ast.error;

import libbun.ast.BNode;
import libbun.util.BField;
import libbun.util.Var;

public class TypeErrorNode extends ErrorNode {
	@BField public BNode ErrorNode;
	public TypeErrorNode(String ErrorMessage, BNode ErrorNode) {
		super(ErrorNode.ParentNode, ErrorNode.SourceToken, ErrorMessage);
		this.ErrorNode = ErrorNode;
	}
	//
	//	public TypeErrorNode(BNode Node, BToken SourceToken, String ErrorMessage) {
	//		super(Node.ParentNode, SourceToken, ErrorMessage);
	//		this.ErrorNode = Node;
	//	}
	@Override public BNode Dup(boolean TypedClone, BNode ParentNode) {
		@Var BNode NewErrorNode =  this.ErrorNode.Dup(TypedClone, ParentNode);
		if(TypedClone) {
			@Var TypeErrorNode NewNode = new TypeErrorNode(this.ErrorMessage, NewErrorNode);
			return this.DupField(TypedClone, NewNode);
		}
		return NewErrorNode;
	}

}
