package libbun.parser.ssa;

import libbun.parser.ast.BGetNameNode;
import libbun.parser.ast.BNode;

public class ValueReplacer extends ZASTTransformer {
	private BNode OldNode;
	private BNode NewNode;
	public ValueReplacer() {
		this.SetTarget(null, null);
	}

	public void SetTarget(BNode OldNode, BNode NewNode) {
		this.OldNode = OldNode;
		this.NewNode = NewNode;
	}

	@Override
	protected void VisitAfter(BNode Node, int Index) {
		if(Node.AST[Index] instanceof BGetNameNode) {
			BGetNameNode GNode = (BGetNameNode) Node.AST[Index];
			PHINode phi = (PHINode) this.NewNode;
			if(phi.EqualsName(GNode)) {
				GNode.VarIndex = phi.GetVarIndex();
			}
		}
	}
}