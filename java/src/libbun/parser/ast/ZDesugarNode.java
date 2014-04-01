package libbun.parser.ast;

import libbun.parser.ZGenerator;
import libbun.parser.ZTypeChecker;
import libbun.util.Field;
import libbun.util.Var;

public class ZDesugarNode extends ZSugarNode {
	//	public final static int _NewNode = 0;
	@Field ZNode OriginalNode;

	public ZDesugarNode(ZNode OriginalNode, ZNode DesugaredNode) {
		super(OriginalNode.ParentNode, null, 1);
		this.OriginalNode = OriginalNode;
		this.SetChild(OriginalNode, ZNode._EnforcedParent);
		this.SetNode(0, DesugaredNode);
	}

	public ZDesugarNode(ZNode OriginalNode, ZNode[] Nodes) {
		super(OriginalNode.ParentNode, null, Nodes.length);
		this.OriginalNode = OriginalNode;
		this.SetChild(OriginalNode, ZNode._EnforcedParent);
		@Var int i = 0;
		while(i < Nodes.length) {
			this.SetNode(i, Nodes[i]);
			i = i + 1;
		}
	}

	@Override public ZDesugarNode DeSugar(ZGenerator Generator, ZTypeChecker TypeChekcer) {
		return this;
	}

}
