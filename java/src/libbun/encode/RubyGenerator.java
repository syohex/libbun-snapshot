package libbun.encode;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.ZCastNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.ZInstanceOfNode;
import libbun.parser.ast.ZLetVarNode;
import libbun.parser.ast.ZThrowNode;
import libbun.parser.ast.ZTryNode;
import libbun.type.ZType;


public class RubyGenerator extends ZSourceGenerator {

	private final ScriptEngineManager EngineManager;
	private final ScriptEngine Engine;

	public RubyGenerator() {
		super("ruby", "1.9.3");
		this.LineFeed = "\n";
		this.Tab = "\t";
		this.LineComment = "#"; // if not, set null
		this.BeginComment = "=begin";
		this.EndComment = "=end";
		this.Camma = ", ";
		this.SemiColon = "";

		this.TrueLiteral = "true";
		this.FalseLiteral = "false";
		this.NullLiteral = "nil";
		this.TopType = "Object";
		this.SetNativeType(ZType.BooleanType, "Object"); // No boolean type in Ruby.
		this.SetNativeType(ZType.IntType, "Fixnum");
		this.SetNativeType(ZType.FloatType, "Float");
		this.SetNativeType(ZType.StringType, "String");

		this.EngineManager = new ScriptEngineManager();
		this.Engine = this.EngineManager.getEngineByName("jruby");

	}

	//	@Override
	//	public Object EvalTopLevelNode(ZNode Node) {
	//		String Code = this.CurrentBuilder.toString();
	//		System.out.println(Code);
	//		this.CurrentBuilder.Clear();
	//		try {
	//			return ((Compilable)this.Engine).compile(Code).eval();
	//		} catch (ScriptException ex) {
	//			ex.printStackTrace();
	//		}
	//		return null;
	//	}

	@Override
	public void VisitBlockNode(ZBlockNode Node) {
		this.CurrentBuilder.Append("do");
		this.CurrentBuilder.Indent();
		throw new RuntimeException("FIXME: don't use for statement");
		//		for(ZNode SubNode : Node.StmtList) {
		//			this.CurrentBuilder.AppendLineFeed();
		//			this.CurrentBuilder.AppendIndent();
		//			this.GenerateCode(SubNode);
		//			this.CurrentBuilder.Append(this.SemiColon);
		//		}
		//		this.CurrentBuilder.UnIndent();
		//		this.CurrentBuilder.AppendLineFeed();
		//		this.CurrentBuilder.AppendIndent();
		//		this.CurrentBuilder.Append("end");
	}

	@Override public void VisitCastNode(ZCastNode Node) {
		// Use method (like 1.to_s) in Ruby.
	}

	@Override public void VisitInstanceOfNode(ZInstanceOfNode Node) {
		// Use method (like "a".is_a?(Object)) in Ruby.
	}

	@Override
	public void VisitThrowNode(ZThrowNode Node) {
		this.CurrentBuilder.Append("raise ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override
	public void VisitTryNode(ZTryNode Node) {
		this.CurrentBuilder.Append("begin");
		this.GenerateCode(null, Node.TryBlockNode());
		if (Node.CatchBlockNode() != null) {
			this.GenerateCode(null, Node.CatchBlockNode());
		}
		if (Node.FinallyBlockNode() != null) {
			this.CurrentBuilder.Append("ensure");
			this.GenerateCode(null, Node.FinallyBlockNode());
		}
	}

	//	@Override
	//	public void VisitCatchNode(ZCatchNode Node) {
	//		this.CurrentBuilder.Append("rescue => ");
	//		//this.VisitType(Node.ExceptionType);
	//		this.CurrentBuilder.Append(Node.GivenName);
	//		this.GenerateCode(null, Node.AST[ZCatchNode._Block]);
	//	}

	//	@Override
	//	public void VisitVarNode(ZVarBlockNode Node) {
	//		this.CurrentBuilder.Append(Node.GetName());
	//		this.CurrentBuilder.AppendToken("=");
	//		this.GenerateCode(null, Node.InitValueNode());
	//	}

	@Override
	protected void VisitParamNode(ZLetVarNode Node) {
		this.CurrentBuilder.Append(Node.GetName());
	}

	@Override public void VisitFunctionNode(ZFunctionNode Node) {
		this.CurrentBuilder.Append("->");
		this.VisitFuncParamNode("(", Node, ")");
		this.GenerateCode(null, Node.BlockNode());
	}

	//	public void VisitFuncDeclNode(ZFunctionNode/ Node) {
	//		this.CurrentBuilder.Append("def ");
	//		this.CurrentBuilder.Append(Node.FuncName);
	//		this.VisitListNode("(", Node, ")");
	//		if (Node.BlockNode() != null) {
	//			this.GenerateCode(Node.BlockNode());
	//		}
	//	}
}
