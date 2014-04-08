package libbun.parser.ssa;

import libbun.ast.BunBlockNode;
import libbun.ast.BNode;
import libbun.ast.LocalDefinedNode;
import libbun.ast.expression.GetNameNode;
import libbun.encode.AbstractGenerator;
import libbun.util.BArray;
import libbun.util.Var;

public class PHINode extends LocalDefinedNode {
	public BArray<BNode>      Args;
	public BArray<BunBlockNode> Blocks;
	public Variable VarRef;
	public Variable BackupValue;
	public String VariableName;

	public PHINode(Variable BackupValue, String VariableName) {
		super(null, null, 0);
		this.BackupValue = BackupValue;
		this.VariableName = VariableName;
		this.Args = new BArray<BNode>(new BNode[0]);
		this.Blocks = new BArray<BunBlockNode>(new BunBlockNode[0]);
		this.Type = NodeLib.GetType(BackupValue.Node);
	}

	public void AddIncoming(int Index, BunBlockNode block, BNode node) {
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

	public boolean EqualsName(GetNameNode Node, AbstractGenerator Generator) {
		return Node.GetUniqueName(Generator).equals(this.VariableName);
	}
}