package libbun.parser.ssa2;

import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.ZLocalDefinedNode;
import libbun.parser.ast.ZNode;
import libbun.util.ZArray;

public class PHINode extends ZLocalDefinedNode {
	public ZArray<ZNode>      Args;
	public ZArray<ZBlockNode> Blocks;
	public ZNode OriginalNode;
	public String VariableName;
	public PHINode(ZNode OriginalNode, String VariableName) {
		super(null, null, 0);
		this.VariableName = VariableName;
		this.Args = new ZArray<ZNode>(new ZNode[0]);
		this.Blocks = new ZArray<ZBlockNode>(new ZBlockNode[0]);
	}

	public void AddIncoming(ZBlockNode block, ZNode node) {
		this.Blocks.add(block);
		this.Args.add(node);
	}

	public boolean IsSameVariable(Variable Var) {
		return this.VariableName.equals(Var.Name);
	}
}