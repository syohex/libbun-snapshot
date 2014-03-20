package libbun.parser.ssa;

import libbun.parser.ast.ZBlockNode;


public class SSAConveter extends ZASTTransformer {
	static final boolean	DEBUG_DOMTREE	= !false;

	public boolean Apply(ZBlockNode Block) {
		ControlFlowGraph cfg = new ControlFlowGraph(Block);
		cfg.ComputeDominanceFrontier();
		cfg.EntryBlock.InsertPHI();
		cfg.EntryBlock.InsertUndefinedVariables();
		cfg.RenamingVariables();
		return true;
	}
}