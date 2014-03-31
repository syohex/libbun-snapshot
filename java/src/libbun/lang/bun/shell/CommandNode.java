package libbun.lang.bun.shell;

import libbun.parser.ast.ZArrayLiteralNode;
import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.ZDesugarNode;
import libbun.parser.ast.ZFuncCallNode;
import libbun.parser.ast.ZGetNameNode;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZSugarNode;
import libbun.parser.ZGenerator;
import libbun.parser.ZToken;
import libbun.parser.ZTypeChecker;
import libbun.type.ZType;
import libbun.util.Field;
import libbun.util.Var;
import libbun.util.ZArray;

public class CommandNode extends ZSugarNode {
	@Field private final ZArray<ZNode> ArgList;
	@Field private ZType RetType = ZType.VarType;
	@Field public CommandNode PipedNextNode;

	public CommandNode(ZNode ParentNode, ZToken Token, String Command) {
		super(ParentNode, Token, 0);
		this.PipedNextNode = null;
		this.ArgList = new ZArray<ZNode>(new ZNode[]{});
		this.AppendArgNode(new ArgumentNode(ParentNode, Command));
	}

	public void AppendArgNode(ZNode Node) {
		this.ArgList.add(this.SetChild(Node, true));
	}

	public ZNode AppendPipedNextNode(CommandNode Node) {
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

	public void SetArgAt(int Index, ZNode ArgNode) {
		ZArray.SetIndex(this.ArgList, Index, ArgNode);
	}

	public ZNode GetArgAt(int Index) {
		return ZArray.GetIndex(this.ArgList, Index);
	}

	public void SetType(ZType Type) {
		this.RetType = Type;
	}

	public ZType RetType() {
		return this.RetType;
	}

	@Override public ZDesugarNode DeSugar(ZGenerator Generator, ZTypeChecker TypeChecker) {
		@Var ZType ContextType = TypeChecker.GetContextType();
		@Var String FuncName = "ExecCommandInt";
		if(this.RetType().IsVarType()) {
			if(ContextType.IsBooleanType() || ContextType.IsIntType() || ContextType.IsStringType()) {
				this.SetType(ContextType);
			}
			else if(ContextType.IsVarType() && !(this.ParentNode instanceof ZBlockNode)) {
				this.SetType(ZType.StringType);
			}
			else {
				this.SetType(ZType.IntType);
			}
		}
		if(this.RetType().IsBooleanType()) {
			FuncName = "ExecCommandBoolean";
		}
		else if(this.RetType().IsStringType()) {
			FuncName = "ExecCommandString";
		}
		@Var ZArrayLiteralNode ArrayNode = new ZArrayLiteralNode(this.ParentNode);
		@Var CommandNode CurrentNode = this;
		while(CurrentNode != null) {
			@Var ZArrayLiteralNode SubArrayNode = new ZArrayLiteralNode(ArrayNode);
			@Var int size = CurrentNode.GetArgSize();
			@Var int i = 0;
			while(i < size) {
				SubArrayNode.Append(CurrentNode.GetArgAt(i));
				i = i + 1;
			}
			ArrayNode.Append(SubArrayNode);
			CurrentNode = CurrentNode.PipedNextNode;
		}
		@Var ZFuncCallNode Node = new ZFuncCallNode(this.ParentNode, new ZGetNameNode(this.ParentNode, null, FuncName));
		Node.Append(ArrayNode);
		return new ZDesugarNode(this, Node);
	}
}