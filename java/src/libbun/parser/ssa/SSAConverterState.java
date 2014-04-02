package libbun.parser.ssa;

public class SSAConverterState {
	public SSAConverterState Prev;
	public JoinNode Node;
	public int BranchIndex;
	SSAConverterState(JoinNode Node, int BranchIndex) {
		this.Prev = null;
		this.Node = Node;
		this.BranchIndex = BranchIndex;
	}

	void SetPrev(SSAConverterState Prev) {
		this.Prev = Prev;
	}
}