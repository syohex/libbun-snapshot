package libbun.parser.ssa2;

public class SSATransformerState {
	public SSATransformerState Prev;
	public JoinNode Node;
	public int BranchIndex;
	SSATransformerState(JoinNode Node, int BranchIndex) {
		this.Prev = null;
		this.Node = Node;
		this.BranchIndex = BranchIndex;
	}

	void SetPrev(SSATransformerState Prev) {
		this.Prev = Prev;
	}
}