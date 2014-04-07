package libbun.parser.ssa;

import libbun.ast.BBlockNode;
import libbun.ast.BNode;
import libbun.ast.ZLocalDefinedNode;
import libbun.ast.expression.BGetNameNode;
import libbun.parser.BGenerator;
import libbun.util.BArray;
import libbun.util.Var;

public class PHINode extends ZLocalDefinedNode {
	public BArray<BNode>      Args;
	public BArray<BBlockNode> Blocks;
	public Variable VarRef;
	public Variable BackupValue;
	public String VariableName;

	public PHINode(Variable BackupValue, String VariableName) {
		super(null, null, 0);
		this.BackupValue = BackupValue;
		this.VariableName = VariableName;
		this.Args = new BArray<BNode>(new BNode[0]);
		this.Blocks = new BArray<BBlockNode>(new BBlockNode[0]);
		this.Type = NodeLib.GetType(BackupValue.Node);
	}

	public void AddIncoming(int Index, BBlockNode block, BNode node) {
		while(Index + 1 > this.Args.size()) {
			this.Args.add(this.BackupValue.Node);
			this.Blocks.add(null);
		}
		BArray.SetIndex(this.Args, Index, node);
		BArray.SetIndex(this.Blocks, Index, block);
	}

	public boolean IsSameVariable(Variable Var) {
		return this.VariableName.equals(Var.Name);
	}

	@Override
	public String toString() {
		@Var String Txt = "PHI[";
		@Var int i = 0;
		while(i < this.Args.size()) {
			BNode Node = BArray.GetIndex(this.Args, i);
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

	public BNode GetArgument(int Index) {
		return BArray.GetIndex(this.Args, Index);
	}

	public boolean EqualsName(BGetNameNode Node, BGenerator Generator) {
		return Node.GetUniqueName(Generator).equals(this.VariableName);
	}
}