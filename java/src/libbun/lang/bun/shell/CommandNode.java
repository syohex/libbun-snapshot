package libbun.lang.bun.shell;

import libbun.ast.BArrayLiteralNode;
import libbun.ast.BBlockNode;
import libbun.ast.BGetNameNode;
import libbun.ast.BNode;
import libbun.ast.ZDesugarNode;
import libbun.ast.ZFuncCallNode;
import libbun.ast.ZSugarNode;
import libbun.parser.BGenerator;
import libbun.parser.BToken;
import libbun.parser.BTypeChecker;
import libbun.type.BType;
import libbun.util.BField;
import libbun.util.Var;
import libbun.util.BArray;

public class CommandNode extends ZSugarNode {
	@BField private final BArray<BNode> ArgList;
	@BField private BType RetType = BType.VarType;
	@BField public CommandNode PipedNextNode;

	public CommandNode(BNode ParentNode, BToken Token, String Command) {
		super(ParentNode, Token, 0);
		this.PipedNextNode = null;
		this.ArgList = new BArray<BNode>(new BNode[]{});
		this.AppendArgNode(new ArgumentNode(ParentNode, Command));
	}

	public void AppendArgNode(BNode Node) {
		this.ArgList.add(this.SetChild(Node, true));
	}

	public BNode AppendPipedNextNode(CommandNode Node) {
		@Var CommandNode CurrentNode = this;
		while(CurrentNode.PipedNextNode != null) {
			CurrentNode = (CommandNode) CurrentNode.PipedNextNode;
		}
		CurrentNode.PipedNextNode = (CommandNode) CurrentNode.SetChild(Node, false);
		return this;
	}

	public int GetArgSize() {
		return this.ArgList.size();
	}

	public void SetArgAt(int Index, BNode ArgNode) {
		BArray.SetIndex(this.ArgList, Index, ArgNode);
	}

	public BNode GetArgAt(int Index) {
		return BArray.GetIndex(this.ArgList, Index);
	}

	public void SetType(BType Type) {
		this.RetType = Type;
	}

	public BType RetType() {
		return this.RetType;
	}

	@Override public ZDesugarNode DeSugar(BGenerator Generator, BTypeChecker TypeChecker) {
		@Var BType ContextType = TypeChecker.GetContextType();
		@Var String FuncName = "ExecCommandInt";
		if(this.RetType().IsVarType()) {
			if(ContextType.IsBooleanType() || ContextType.IsIntType() || ContextType.IsStringType()) {
				this.SetType(ContextType);
			}
			else if(ContextType.IsVarType() && !(this.ParentNode instanceof BBlockNode)) {
				this.SetType(BType.StringType);
			}
			else {
				this.SetType(BType.IntType);
			}
		}
		if(this.RetType().IsBooleanType()) {
			FuncName = "ExecCommandBoolean";
		}
		else if(this.RetType().IsStringType()) {
			FuncName = "ExecCommandString";
		}
		@Var BArrayLiteralNode ArrayNode = new BArrayLiteralNode(this.ParentNode);
		@Var CommandNode CurrentNode = this;
		while(CurrentNode != null) {
			@Var BArrayLiteralNode SubArrayNode = new BArrayLiteralNode(ArrayNode);
			@Var int size = CurrentNode.GetArgSize();
			@Var int i = 0;
			while(i < size) {
				SubArrayNode.Append(CurrentNode.GetArgAt(i));
				i = i + 1;
			}
			ArrayNode.Append(SubArrayNode);
			CurrentNode = CurrentNode.PipedNextNode;
		}
		@Var ZFuncCallNode Node = new ZFuncCallNode(this.ParentNode, new BGetNameNode(this.ParentNode, null, FuncName));
		Node.Append(ArrayNode);
		return new ZDesugarNode(this, Node);
	}
}