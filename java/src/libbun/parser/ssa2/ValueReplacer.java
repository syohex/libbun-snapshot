package libbun.parser.ssa2;

import libbun.parser.ast.ZGetNameNode;
import libbun.parser.ast.ZNode;

public class ValueReplacer extends ZASTTransformer {
	private ZNode OldNode;
	private ZNode NewNode;
	public ValueReplacer() {
		this.SetTarget(null, null);
	}

	public void SetTarget(ZNode OldNode, ZNode NewNode) {
		this.OldNode = OldNode;
		this.NewNode = NewNode;
	}

	@Override
	protected void VisitAfter(ZNode Node, int Index) {
		if(Node.AST[Index] instanceof ZGetNameNode) {
			ZGetNameNode GNode = (ZGetNameNode) Node.AST[Index];
			PHINode phi = (PHINode) this.NewNode;
			if(GNode.GetName().equals(phi.GetName())) {
				GNode.VarIndex = phi.GetVarIndex();
			}
		}
	}
}