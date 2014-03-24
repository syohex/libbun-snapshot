package libbun.parser.ssa2;

import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.ZGetNameNode;
import libbun.parser.ast.ZIfNode;
import libbun.parser.ast.ZLetVarNode;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZSetNameNode;
import libbun.parser.ast.ZVarBlockNode;
import libbun.parser.ast.ZWhileNode;
import libbun.util.Var;
import libbun.util.ZArray;

public class SSATransformer2 extends ZASTTransformer {
	ZBlockNode CurrentJoinNode;
	ZArray<Variable> LocalVariables;
	ValueReplacer Replacer;
	public SSATransformer2() {
		this.LocalVariables = null;
		this.Replacer = new ValueReplacer();
	}

	private Variable FindVariable(String Name) {
		@Var int i = this.LocalVariables.size() - 1;
		while(i >= 0) {
			Variable V = ZArray.GetIndex(this.LocalVariables, i);
			if(V != null && V.Name.equals(Name)) {
				return V;
			}
		}
		assert(false); // unreachable
		return null;
	}

	private void AddVariable(Variable V) {
		V.Index = this.LocalVariables.size();
		this.LocalVariables.add(V);
	}

	private PHINode FindPHINodeFromBlock(ZBlockNode Block, Variable Var) {
		@Var int i = 0;
		while(i < Block.GetListSize()) {
			ZNode Node = Block.GetListAt(i);
			if(Node instanceof PHINode) {
				PHINode PNode = (PHINode) Node;
				if(PNode.IsSameVariable(Var)) {
					return PNode;
				}
			}
			i = i + 1;
		}
		return null;
	}

	private void InsertPHI(ZNode JoinNode, int Index, Variable NewVal, Variable OldVal) {
		assert(JoinNode instanceof ZBlockNode);
		ZBlockNode JoinBlock = (ZBlockNode) JoinNode;
		// 1. Find PHINode from JoinBlock
		@Var PHINode phi = this.FindPHINodeFromBlock(JoinBlock, OldVal);

		// 2. If PHINode for NewVal is not defined, create new one.
		if(phi == null) {
			phi = new PHINode(NewVal.Node, NewVal.Name);
			JoinBlock.Append(phi);
			if(JoinNode instanceof ZWhileNode) {
				this.ReplaceNodeWith(JoinNode.AST[ZWhileNode._Cond], OldVal, phi);
			}
		}
		// 3. Added Incomming Variable infomation
		phi.AddIncoming(null/*FIXME*/, NewVal.Node);
	}

	private void ReplaceNodeWith(ZNode Node, Variable OldVal, PHINode PHI) {
		this.Replacer.SetTarget(OldVal.Node, PHI);
		Node.Accept(this.Replacer);
	}

	private void CommitPHINode(ZNode Node) {
	}

	private void MakeCurrentVariableTo(Variable NewVal) {
	}

	@Override
	public void VisitGetNameNode(ZGetNameNode Node) {
		Variable V = this.FindVariable(Node.GetName());
	}

	@Override
	public void VisitSetNameNode(ZSetNameNode Node) {
		Variable V = this.FindVariable(Node.GetName());
	}

	@Override
	public void VisitVarBlockNode(ZVarBlockNode Node) {
		Variable V = new Variable(Node.VarDeclNode().GetName(), Node);
		this.AddVariable(V);
		for (int i = 0; i < Node.GetListSize(); i++) {
			Node.GetListAt(i).Accept(this);
		}
	}

	@Override
	public void VisitIfNode(ZIfNode Node) {
		Node.CondNode().Accept(this);
		Node.ThenNode().Accept(this);
		Node.ElseNode().Accept(this);
		this.CommitPHINode(Node);
	}

	@Override
	public void VisitWhileNode(ZWhileNode Node) {
		Node.CondNode().Accept(this);
		Node.BlockNode().Accept(this);
		this.CommitPHINode(Node);
	}

	@Override
	public void VisitFunctionNode(ZFunctionNode Node) {
		this.LocalVariables = new ZArray<Variable>(new Variable[0]);

		for(int i = 0; i < Node.GetListSize(); i++) {
			ZLetVarNode ParamNode = Node.GetParamNode(i);
			this.AddVariable(new Variable(ParamNode.GetName(), ParamNode));
		}
		Node.BlockNode().Accept(this);
	}

}
