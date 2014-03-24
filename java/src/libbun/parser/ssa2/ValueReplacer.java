package libbun.parser.ssa2;

import libbun.parser.ast.ZNode;

public class ValueReplacer extends ZASTTransformer {
	ZNode OldNode;
	ZNode NewNode;
	public ValueReplacer() {
		this.SetTarget(null, null);
	}

	public void SetTarget(ZNode OldNode, ZNode NewNode) {
		this.OldNode = OldNode;
		this.NewNode = NewNode;
	}

	@Override
	protected void VisitAfter(ZNode Node, int Index) {
		if(Node == this.OldNode) {
			Node.AST[Index] = this.NewNode;
		}
	}
}