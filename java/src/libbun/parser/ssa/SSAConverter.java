package libbun.parser.ssa;

import java.util.HashMap;

import libbun.parser.ast.ZAndNode;
import libbun.parser.ast.ZBinaryNode;
import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.ZGetNameNode;
import libbun.parser.ast.ZIfNode;
import libbun.parser.ast.ZLetVarNode;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZSetNameNode;
import libbun.parser.ast.ZVarBlockNode;
import libbun.parser.ast.ZWhileNode;
import libbun.type.ZType;
import libbun.util.Var;
import libbun.util.ZArray;
import libbun.util.ZMap;

/**
 * @see
 * Brandis, M. M. and Moessenboeck, H.: Single-pass Generation of Static
 * Single-assignment Form for Structured Languages, ACM Trans.
 * Program. Lang. Syst., Vol. 16, No. 6, pp. 1684-1698
 */

public class SSAConverter extends ZASTTransformer {
	private static final int IfThenBranchIndex = 0;
	private static final int IfElseBranchIndex = 1;
	private static final int WhileBodyBranchIndex = IfElseBranchIndex;

	public SSATransformerState State;
	public ZArray<Variable> LocalVariables;
	public ValueReplacer Replacer;
	public ZMap<Integer> ValueNumber;
	private final HashMap<ZNode, ZArray<Variable>> CurVariableTableBefore;
	private final HashMap<ZNode, ZArray<Variable>> CurVariableTableAfter;

	public SSAConverter() {
		this.LocalVariables = null;
		this.Replacer = new ValueReplacer();
		this.State = new SSATransformerState(null, -1);
		this.ValueNumber = new ZMap<Integer>(ZType.IntType);
		this.CurVariableTableBefore = new HashMap<ZNode, ZArray<Variable>>();
		this.CurVariableTableAfter = new HashMap<ZNode, ZArray<Variable>>();
	}

	private void RecordListOfVariablesBeforeVisit(ZNode Node) {
		this.CurVariableTableBefore.put(Node, this.CloneCurrentValues());
	}

	private void RecordListOfVariablesAfterVisit(ZNode Node) {
		this.CurVariableTableAfter.put(Node, this.CloneCurrentValues());
	}

	/**
	 * Returns the variable information prior to processing the Node.
	 * @param Node
	 * @return	List of information of local variables.
	 * 			The information contains variable name and variable index
	 * example:
	 *   int x = 0;
	 *   int y = 0;
	 *   x = y;
	 *  GetCurrentVariablesBefore(int x = 0) returns []
	 *  GetCurrentVariablesBefore(int y = 0) returns [(x, 0)]
	 *  GetCurrentVariablesBefore(x = y    ) returns [(x, 0), (y,0)]
	 */
	public ZArray<Variable> GetCurrentVariablesBefore(ZNode Node) {
		return this.CurVariableTableBefore.get(Node);
	}

	/**
	 * Returns the variable information after processing the Node.
	 * @param Node
	 * @return	List of information of local variables.
	 * 			The information contains variable name and variable index
	 * example:
	 *   int x = 0;
	 *   int y = 0;
	 *   x = y;
	 *  GetCurrentVariablesAfter(int x = 0) returns []
	 *  GetCurrentVariablesAfter(int y = 0) returns [(x, 0)]
	 *  GetCurrentVariablesAfter(x = y    ) returns [(x, 0), (y,0)]
	 */
	public ZArray<Variable> GetCurrentVariablesAfter(ZNode Node) {
		return this.CurVariableTableAfter.get(Node);
	}

	private void PushState(SSATransformerState State) {
		State.SetPrev(this.State);
		this.State = State;
	}

	private void PopState() {
		this.State = this.State.Prev;
	}

	private JoinNode GetCurrentJoinNode() {
		return this.State.Node;
	}

	private int GetCurrentBranchIndex() {
		return this.State.BranchIndex;
	}

	private void SetCurrentBranchIndex(int BranchIndex) {
		this.State.BranchIndex = BranchIndex;
	}

	private JoinNode GetParentJoinNode() {
		return this.State.Prev.Node;
	}

	private int GetParentBranchIndex() {
		return this.State.Prev.BranchIndex;
	}

	private int GetVariableIndex(String Name) {
		@Var int i = this.LocalVariables.size() - 1;
		while(i >= 0) {
			@Var Variable V = ZArray.GetIndex(this.LocalVariables, i);
			if(V != null && V.Name.equals(Name)) {
				return i;
			}
			i = i - 1;
		}
		assert(false); // unreachable
		return -1;
	}

	private Variable FindVariable(String Name) {
		return ZArray.GetIndex(this.LocalVariables, this.GetVariableIndex(Name));
	}

	private void RemoveVariable(String Name) {
		ZArray.SetIndex(this.LocalVariables, this.GetVariableIndex(Name), null);
	}

	private void UpdateVariable(Variable NewVal) {
		@Var int Index = this.GetVariableIndex(NewVal.Name);
		ZArray.SetIndex(this.LocalVariables, Index, NewVal);
	}

	private void AddVariable(Variable V) {
		this.UpdateValueNumber(V, false);
		this.LocalVariables.add(V);
	}

	private int UpdateValueNumber(Variable V, boolean UpdateValue) {
		@Var Integer Num = this.ValueNumber.GetOrNull(V.Name);
		if(Num == null) {
			Num = 0;
		}
		if(Num < V.Index) {
			Num = V.Index;
		}
		if(UpdateValue) {
			Num = Num + 1;
		}
		this.ValueNumber.put(V.Name, Num);
		return Num;
	}

	private int GetRefreshNumber(Variable Val) {
		return this.UpdateValueNumber(Val, true);
	}

	private ZArray<Variable> CloneCurrentValues() {
		return new ZArray<Variable>(this.LocalVariables.CompactArray());
	}

	private void InsertPHI(JoinNode JNode, int BranchIndex, Variable OldVal, Variable NewVal) {
		// 1. Find PHINode from JoinNode
		@Var PHINode phi = JNode.FindPHINode(OldVal);

		// 2. If PHINode for OldVal.Name is not defined, create new one.
		if(phi == null) {
			phi = new PHINode(OldVal, NewVal.Name);
			phi.VarRef = new Variable(OldVal.Name, this.GetRefreshNumber(OldVal), phi);
			JNode.Append(phi);
			if(this.GetCurrentJoinNode().ParentNode instanceof ZWhileNode) {
				ZWhileNode WNode = (ZWhileNode) this.GetCurrentJoinNode().ParentNode;
				this.ReplaceNodeWith(WNode, OldVal, phi);
			}
		}
		// 3. Added Incomming Variable infomation
		phi.AddIncoming(BranchIndex, null/*FIXME*/, NewVal.Node);
	}

	private void ReplaceNodeWith(ZNode Node, Variable OldVal, PHINode PHI) {
		this.Replacer.SetTarget(OldVal.Node, PHI);
		Node.Accept(this.Replacer);
	}

	private void MakeCurrentVariableTo(Variable NewVal) {
		this.UpdateVariable(NewVal);
	}

	private void CommitPHINode(JoinNode JNode) {
		if(this.GetParentJoinNode() == null) {
			return;
		}
		@Var int i = JNode.size() - 1;
		while(i >= 0) {
			PHINode phi = JNode.ListAt(i);
			ZNode node;
			if (JNode.isJoinNodeOfRepeatNode()) {
				//this.State.Prev != null && this.GetParentJoinNode().ParentNode instanceof ZWhileNode
				node = phi.GetArgument(phi.Args.size() - 1);
			} else {
				node = phi.GetArgument(this.GetCurrentBranchIndex());
			}
			Variable val = this.FindVariable(NodeLib.GetVarName(node));
			this.MakeCurrentVariableTo(val);
			this.InsertPHI(this.GetParentJoinNode(), this.GetParentBranchIndex(), phi.BackupValue, val);
			i = i - 1;
		}
	}

	/**
	 * Merge JoinNode into a parent node of TargetNode
	 * @param TargetNode
	 * @param JNode
	 * Example.
	 * TargetNode := if(y) {...} else { ... }
	 * JNode      := [x2 = phi(x0, x1)]
	 *    Before      |   After
	 * function f() { | function f() {
	 *   if(y) {      |  if(y) {
	 *     x0 = ...   |    x0 = ...
	 *   } else {     |  } else {
	 *     x1 = ...   |    x1 = ...
	 *   }            |  }
	 *                |  x2 = phi(x0, x1)
	 * }              | }
	 */
	private void RemoveJoinNode(ZNode TargetNode, JoinNode JNode) {
		@Var ZBlockNode Parent = TargetNode.GetScopeBlockNode();
		@Var int Index = 0;
		assert(Parent != null);
		while(Index < Parent.GetListSize()) {
			ZNode Node = Parent.GetListAt(Index);
			Index = Index + 1;
			if(Node == TargetNode) {
				break;
			}
		}
		assert(Index < Parent.GetListSize());

		if(TargetNode instanceof ZIfNode) {
			// JoinNode for ZIfNode is placed after if-statement.
			@Var int i = JNode.size() - 1;
			while(i >= 0) {
				PHINode phi = JNode.ListAt(i);
				Parent.InsertListAt(Index, phi);
				this.UpdateVariable(phi.VarRef);
				i = i - 1;
			}
		} else if (TargetNode instanceof ZWhileNode) {
			// JoinNode for WhileNode is placed at a header of loop.
			// ... while((x1 = phi() && i1 = phi()) && x1 == true) {...}
			@Var ZWhileNode WNode = (ZWhileNode) TargetNode;
			@Var ZNode CondNode = WNode.CondNode();
			@Var int i = JNode.size() - 1;
			while(i >= 0) {
				@Var PHINode phi = JNode.ListAt(i);
				@Var ZAndNode And = new ZAndNode(Parent, null, phi, null);
				And.SetNode(ZBinaryNode._Right, CondNode);
				And.Type = ZType.BooleanType;
				CondNode = And;
				this.ReplaceNodeWith(TargetNode, phi.VarRef, phi);
				this.UpdateVariable(phi.VarRef);
				i = i - 1;
			}
			WNode.SetNode(ZWhileNode._Cond, CondNode);
		}
	}

	@Override
	public void VisitGetNameNode(ZGetNameNode Node) {
		@Var Variable V = this.FindVariable(Node.GetName());
		Node.VarIndex = V.Index;
	}

	@Override
	public void VisitSetNameNode(ZSetNameNode Node) {
		@Var Variable OldVal = this.FindVariable(Node.GetName());
		@Var Variable NewVal = new Variable(OldVal.Name, this.GetRefreshNumber(OldVal), Node);
		this.UpdateVariable(NewVal);
		Node.VarIndex = NewVal.Index;
		this.InsertPHI(this.GetCurrentJoinNode(), this.GetCurrentBranchIndex(), OldVal, NewVal);
	}

	@Override
	public void VisitVarBlockNode(ZVarBlockNode Node) {
		@Var Variable V = new Variable(Node.VarDeclNode().GetName(), 0, Node);
		this.AddVariable(V);
		@Var int i = 0;
		while(i < Node.GetListSize()) {
			Node.GetListAt(i).Accept(this);
			i = i + 1;
		}
		this.RemoveVariable(V.Name);
	}

	@Override
	public void VisitIfNode(ZIfNode Node) {
		this.PushState(new SSATransformerState(new JoinNode(Node), 0));
		this.RecordListOfVariablesBeforeVisit(Node);
		Node.CondNode().Accept(this);
		this.SetCurrentBranchIndex(IfThenBranchIndex);
		this.RecordListOfVariablesBeforeVisit(Node.ThenNode());
		Node.ThenNode().Accept(this);
		this.RecordListOfVariablesAfterVisit(Node.ThenNode());
		if(Node.HasElseNode()) {
			this.RecordListOfVariablesBeforeVisit(Node.ElseNode());
			this.SetCurrentBranchIndex(IfElseBranchIndex);
			Node.ElseNode().Accept(this);
			this.RecordListOfVariablesAfterVisit(Node.ElseNode());
		}
		this.RecordListOfVariablesAfterVisit(Node);
		this.RemoveJoinNode(Node, this.GetCurrentJoinNode());
		this.CommitPHINode(this.GetCurrentJoinNode());
		this.PopState();
	}

	@Override
	public void VisitWhileNode(ZWhileNode Node) {
		this.PushState(new SSATransformerState(new JoinNode(Node), 0));
		this.RecordListOfVariablesBeforeVisit(Node);
		Node.CondNode().Accept(this);
		this.RecordListOfVariablesBeforeVisit(Node.BlockNode());
		this.SetCurrentBranchIndex(WhileBodyBranchIndex);
		Node.BlockNode().Accept(this);
		this.RecordListOfVariablesAfterVisit(Node.BlockNode());
		this.RecordListOfVariablesAfterVisit(Node);
		this.RemoveJoinNode(Node, this.GetCurrentJoinNode());
		this.CommitPHINode(this.GetCurrentJoinNode());
		this.PopState();
	}

	@Override
	public void VisitFunctionNode(ZFunctionNode Node) {
		this.LocalVariables = new ZArray<Variable>(new Variable[0]);
		@Var int i = 0;
		while(i < Node.GetListSize()) {
			ZLetVarNode ParamNode = Node.GetParamNode(i);
			this.AddVariable(new Variable(ParamNode.GetName(), 0, ParamNode));
			i = i + 1;
		}
		Node.BlockNode().Accept(this);
	}
}
