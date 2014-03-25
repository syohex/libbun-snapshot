package libbun.parser.ssa2;

import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.ZLocalDefinedNode;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZArray;

public class PHINode extends ZLocalDefinedNode {
	public ZArray<ZNode>      Args;
	public ZArray<ZBlockNode> Blocks;
	public Variable VarRef;
	public Variable BackupValue;
	public String VariableName;

	public PHINode(Variable BackupValue, String VariableName) {
		super(null, null, 0);
		this.BackupValue = BackupValue;
		this.VariableName = VariableName;
		this.Args = new ZArray<ZNode>(new ZNode[0]);
		this.Blocks = new ZArray<ZBlockNode>(new ZBlockNode[0]);
		this.Type = NodeLib.GetType(BackupValue);
	}

	public void AddIncoming(int Index, ZBlockNode block, ZNode node) {
		while(Index + 1 > this.Args.size()) {
			this.Args.add(this.BackupValue.Node);
			this.Blocks.add(null);
		}
		ZArray.SetIndex(this.Args, Index, node);
		ZArray.SetIndex(this.Blocks, Index, block);
	}

	public boolean IsSameVariable(Variable Var) {
		return this.VariableName.equals(Var.Name);
	}

	@Override
	public String toString() {
		@Var String Txt = "PHI[";
		@Var int i = 0;
		while(i < this.Args.size()) {
			ZNode Node = ZArray.GetIndex(this.Args, i);
			if (i != 0) {
				Txt += ", ";
			}
			Txt += Node.getClass().toString();
			i = i + 1;
		}
		Txt += "]";
		return Txt;
	}

	public int GetVarIndex() {
		return this.VarRef.Index;
	}

	public String GetName() {
		return this.VariableName;
	}

	public ZNode GetArgument(int Index) {
		return ZArray.GetIndex(this.Args, Index);
	}
}