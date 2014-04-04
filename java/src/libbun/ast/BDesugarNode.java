package libbun.ast;

import libbun.parser.BGenerator;
import libbun.parser.BTypeChecker;
import libbun.util.BField;
import libbun.util.Var;

public class BDesugarNode extends BSugarNode {
	//	public final static int _NewNode = 0;
	@BField BNode OriginalNode;

	public BDesugarNode(BNode OriginalNode, BNode DesugardNode) {
		super(OriginalNode.ParentNode, null, 1);
		this.OriginalNode = OriginalNode;
		this.SetChild(OriginalNode, BNode._EnforcedParent);
		this.SetNode(0, DesugardNode);
	}

	public BDesugarNode(BNode OriginalNode, BNode[] DesugarNodes) {
		super(OriginalNode.ParentNode, null, DesugarNodes.length);
		this.OriginalNode = OriginalNode;
		this.SetChild(OriginalNode, BNode._EnforcedParent);
		@Var int i = 0;
		while(i < DesugarNodes.length) {
			this.SetNode(i, DesugarNodes[i]);
			i = i + 1;
		}
	}

	@Override public BDesugarNode DeSugar(BGenerator Generator, BTypeChecker TypeChekcer) {
		return this;
	}

}
