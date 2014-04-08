// ***************************************************************************
// Copyright (c) 2013, JST/CREST DEOS project authors. All rights reserved.
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// *  Redistributions of source code must retain the above copyright notice,
//    this list of conditions and the following disclaimer.
// *  Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
// TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
// EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
// PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
// OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
// OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
// ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// **************************************************************************

package libbun.encode;

import libbun.ast.BunBlockNode;
import libbun.ast.GroupNode;
import libbun.ast.BNode;
import libbun.ast.binary.BinaryOperatorNode;
import libbun.ast.binary.BunOrNode;
import libbun.ast.binary.BunAndNode;
import libbun.ast.binary.ComparatorNode;
import libbun.ast.decl.BunFunctionNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.ast.decl.BunVarBlockNode;
import libbun.ast.error.ErrorNode;
import libbun.ast.expression.FuncCallNode;
import libbun.ast.expression.BunFuncNameNode;
import libbun.ast.expression.GetIndexNode;
import libbun.ast.expression.SetIndexNode;
import libbun.ast.expression.SetNameNode;
import libbun.ast.literal.BunArrayLiteralNode;
import libbun.ast.statement.BunBreakNode;
import libbun.ast.statement.BunIfNode;
import libbun.ast.statement.BunReturnNode;
import libbun.ast.statement.BunThrowNode;
import libbun.ast.statement.BunTryNode;
import libbun.ast.statement.BunWhileNode;
import libbun.ast.unary.BunNotNode;
import libbun.ast.unary.UnaryOperatorNode;
import libbun.parser.BToken;
import libbun.type.BFuncType;
import libbun.type.BType;
import libbun.util.Var;
import libbun.util.ZenMethod;

public class CommonLispGenerator extends OldSourceGenerator {
	private boolean hasMain = false;
	public CommonLispGenerator() {
		super("cl", "CommonLisp");

		this.LineComment = "#"; // if not, set null
		this.BeginComment = null; //"'''";
		this.EndComment = null; //"'''";
		this.Camma = " ";
		this.SemiColon = "";

		this.TrueLiteral = "t";
		this.FalseLiteral = "nil";
		this.NullLiteral = "nil";

		this.AndOperator = "and";
		this.OrOperator = "or";
		this.NotOperator = "not ";

		this.TopType = "object";
		this.SetNativeType(BType.BooleanType, "bool");
		this.SetNativeType(BType.IntType, "int");
		this.SetNativeType(BType.FloatType, "float");
		this.SetNativeType(BType.StringType, "str");
	}

	@Override public void VisitSetNameNode(SetNameNode Node) {
		this.CurrentBuilder.Append("(setq ");
		this.VisitGetNameNode(Node.NameNode());
		this.CurrentBuilder.Append(" ");
		this.GenerateCode(null, Node.ExprNode());
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitUnaryNode(UnaryOperatorNode Node) {
		this.CurrentBuilder.Append("(");
		this.CurrentBuilder.Append(Node.SourceToken.GetText());
		this.CurrentBuilder.Append(" ");
		this.GenerateCode(null, Node.RecvNode());
		this.CurrentBuilder.Append(")");
	}

	@Override protected void GenerateSurroundCode(BNode Node) {
		this.GenerateCode(null, Node);
	}

	private String GetBinaryOperator(BToken Token) {
		if(Token.EqualsText("==")) {
			return "equal";
		} else if(Token.EqualsText("%")) {
			return "mod";
		} else if(Token.EqualsText("/")) {
			return "/";
		}
		return Token.GetText();
	}

	@Override public void VisitBinaryNode(BinaryOperatorNode Node) {
		this.CurrentBuilder.Append("(");
		String operator = GetBinaryOperator(Node.SourceToken);
		if (operator.equals("/")) {
			this.CurrentBuilder.Append("floor" + " ");
			this.CurrentBuilder.Append("(");
		}
		this.CurrentBuilder.Append(operator);
		this.CurrentBuilder.Append(" ");
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.Append(" ");
		this.GenerateCode(null, Node.RightNode());
		if (operator.equals("/")) {
			this.CurrentBuilder.Append(")");
		}
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitComparatorNode(ComparatorNode Node) {
		this.CurrentBuilder.Append("(");
		String operator = this.GetBinaryOperator(Node.SourceToken);
		if (operator.equals("!=")) {
			this.CurrentBuilder.Append("not" + " ");
			this.CurrentBuilder.Append("(equal");
		} else {
			this.CurrentBuilder.Append(operator);
		}
		this.CurrentBuilder.Append(" ");
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.Append(" ");
		this.GenerateCode(null, Node.RightNode());
		if (operator.equals("!=")) {
			this.CurrentBuilder.Append(")");
		}
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitAndNode(BunAndNode Node) {
		this.CurrentBuilder.Append("(and ");
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.Append(" ");
		this.GenerateCode(null, Node.RightNode());
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitOrNode(BunOrNode Node) {
		this.CurrentBuilder.Append("(or ");
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.Append(" ");
		this.GenerateCode(null, Node.RightNode());
		this.CurrentBuilder.Append(")");
	}

	//
	// Visitor API
	//
	@Override public void VisitWhileNode(BunWhileNode Node) {
		this.CurrentBuilder.Append("(loop while ");
		this.GenerateCode(null, Node.CondNode());
		this.CurrentBuilder.AppendNewLine();
		this.CurrentBuilder.Append("do");
		this.CurrentBuilder.AppendNewLine();
		this.GenerateCode(null, Node.BlockNode());
		this.CurrentBuilder.Append(")");
	}

	@Override
	protected void VisitVarDeclNode(BunLetVarNode Node) {
		this.CurrentBuilder.Append("(", this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()), " ");
		this.GenerateCode(null, Node.InitValueNode());
		this.CurrentBuilder.Append(")");
		if(Node.HasNextVarNode()) {
			this.VisitVarDeclNode(Node.NextVarNode());
		}
	}

	@Override
	protected void VisitStmtList(BunBlockNode BlockNode) {
		@Var int Size = BlockNode.GetListSize();
		if(Size == 0) {
			this.CurrentBuilder.Append("()");
		}
		else if(Size == 1) {
			this.GenerateStatement(BlockNode.GetListAt(0));
		}
		else {
			this.CurrentBuilder.OpenIndent("(progn");
			@Var int i = 0;
			while (i < BlockNode.GetListSize()) {
				@Var BNode SubNode = BlockNode.GetListAt(i);
				this.GenerateStatement(SubNode);
				i = i + 1;
			}
			this.CurrentBuilder.CloseIndent(")");
		}
	}

	@Override public void VisitBlockNode(BunBlockNode Node) {
		this.VisitStmtList(Node);
	}

	@Override public void VisitVarBlockNode(BunVarBlockNode Node) {
		this.CurrentBuilder.Append("(let (");
		this.VisitVarDeclNode(Node.VarDeclNode());
		this.CurrentBuilder.Append(")");
		this.VisitStmtList(Node);
		this.CurrentBuilder.Append(")");
	}


	//	@Override public void VisitTrinaryNode(ZenTrinaryNode Node) {
	//		this.CurrentBuilder.Append("(if  ");
	//		Node.AST[ZIfNode.Cond].Accept(this);
	//		this.CurrentBuilder.Append(" ");
	//		Node.AST[ZIfNode.Then].Accept(this);
	//		this.CurrentBuilder.Append(" ");
	//		Node.AST[ZIfNode.Else].Accept(this);
	//		this.CurrentBuilder.Append(")");
	//	}

	@Override public void VisitIfNode(BunIfNode Node) {
		this.CurrentBuilder.Append("(if  ");
		this.GenerateCode(null, Node.CondNode());
		this.CurrentBuilder.Append(" ");
		this.GenerateCode(null, Node.ThenNode());
		this.CurrentBuilder.Append(" ");
		if(Node.HasElseNode()) {
			this.GenerateCode(null, Node.ElseNode());
		}
		else {
			this.CurrentBuilder.Append("nil");
		}
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitFuncCallNode(FuncCallNode Node) {
		this.CurrentBuilder.Append("(");
		@Var BunFuncNameNode FuncNameNode = Node.FuncNameNode();
		if(FuncNameNode != null) {
			this.GenerateFuncName(FuncNameNode);
		}
		else {
			this.CurrentBuilder.Append("funcall ");
			this.GenerateCode(null, Node.FunctorNode());
		}
		this.VisitListNode(" ", Node, " ");
		this.CurrentBuilder.Append(")");
	}

	private BunFunctionNode LookupFunctionNode(BNode Node) {
		while(Node != null) {
			if(Node instanceof BunFunctionNode) {
				return (BunFunctionNode)Node;
			}
			Node = Node.ParentNode;
		}
		return null;
	}

	private String ClFunctionName(BunFunctionNode FuncNode) {
		String FuncName = FuncNode.FuncName();
		if (FuncName != null) {
			if (FuncName.equals("main")) {
				return "main";
			} else {
				return FuncNode.GetSignature();
			}
		} else {
			// wrapped nil block
			return "nil";
		}
	}

	@Override public void VisitReturnNode(BunReturnNode Node) {
		@Var BunFunctionNode FuncNode = this.LookupFunctionNode(Node);
		if(FuncNode != null) {
			this.CurrentBuilder.Append("(return-from ", ClFunctionName(FuncNode), " ");
		}
		else {
			this.CurrentBuilder.Append("(return ");
		}
		if(Node.HasReturnExpr()) {
			this.GenerateCode(null, Node.ExprNode());
		}
		else {
			this.CurrentBuilder.Append("nil");
		}
		this.CurrentBuilder.Append(")");
	}

	@Override protected void VisitParamNode(BunLetVarNode Node) {
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
	}

	@Override public void VisitFunctionNode(BunFunctionNode Node) {
		if(!Node.IsTopLevelDefineFunction()) {
			this.CurrentBuilder.Append("#'(lambda ");
			this.VisitFuncParamNode("(", Node, ")");
			this.CurrentBuilder.Append("(block nil ");
			this.GenerateCode(null, Node.BlockNode());
			this.CurrentBuilder.Append("))");
		}
		else {
			@Var BFuncType FuncType = Node.GetFuncType();
			this.CurrentBuilder.Append("(defun ");
			this.CurrentBuilder.Append(ClFunctionName(Node));
			this.VisitFuncParamNode(" (", Node, ")");
			this.GenerateCode(null, Node.BlockNode());
			this.CurrentBuilder.Append(")");
			if(Node.IsExport) {
				if(Node.FuncName().equals("main")) {
					this.hasMain = true;
				}
			}
			if(this.IsMethod(Node.FuncName(), FuncType)) {
				//				this.CurrentBuilder.Append(this.NameMethod(FuncType.GetRecvType(), Node.FuncName));
				//				this.CurrentBuilder.Append(" = ", FuncType.StringfySignature(Node.FuncName));
				//				this.CurrentBuilder.AppendLineFeed();
			}
		}
	}


	@Override public void VisitErrorNode(ErrorNode Node) {
		this.CurrentBuilder.Append("(error ");
		this.CurrentBuilder.Append(Node.ErrorMessage);
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitTryNode(BunTryNode Node) {
		this.CurrentBuilder.Append("(unwind-protect ");
		this.CurrentBuilder.Append("(handler-case ");
		this.GenerateCode(null, Node.TryBlockNode());
		if(Node.HasCatchBlockNode()) {
			@Var String VarName = this.NameUniqueSymbol("e");
			this.CurrentBuilder.AppendNewLine("(error (", VarName, ")");
			this.VisitStmtList(Node.CatchBlockNode());
			this.CurrentBuilder.AppendNewLine(")");
		}
		this.CurrentBuilder.Append(")");
		if(Node.HasFinallyBlockNode()) {
			this.GenerateCode(null, Node.FinallyBlockNode());
		}
		this.CurrentBuilder.Append(")");
	}


	@Override @ZenMethod protected void Finish(String FileName) {
		if(this.hasMain) {
			this.CurrentBuilder.AppendNewLine("(main)");
			this.CurrentBuilder.AppendLineFeed();
		}
	}

	@Override public void VisitGroupNode(GroupNode Node) {
		this.GenerateCode2("", null, Node.ExprNode(), "");
	}

	@Override public void VisitNotNode(BunNotNode Node) {
		this.CurrentBuilder.Append("(");
		this.CurrentBuilder.AppendToken(this.NotOperator);
		this.GenerateSurroundCode(Node.RecvNode());
		this.CurrentBuilder.Append(") ");
	}

	@Override public void VisitLetNode(BunLetVarNode Node) {
		this.CurrentBuilder.AppendNewLine("(setf ", Node.GetUniqueName(this), "");
		this.CurrentBuilder.Append(" ");
		this.GenerateCode(null, Node.InitValueNode());
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitThrowNode(BunThrowNode Node) {
		this.CurrentBuilder.Append("(throw nil ");
		this.GenerateCode(null, Node.ExprNode());
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitBreakNode(BunBreakNode Node) {
		this.CurrentBuilder.Append("(return)");
	}

	@Override public void VisitArrayLiteralNode(BunArrayLiteralNode Node) {
		this.VisitListNode("'(", Node, ")");
	}

	@Override public void VisitGetIndexNode(GetIndexNode Node) {
		this.CurrentBuilder.Append("(");
		if (Node.RecvNode().Type == BType.StringType) {
			this.CurrentBuilder.Append("string (");
			this.CurrentBuilder.Append("aref ");
			this.GenerateCode(null, Node.RecvNode());
			this.CurrentBuilder.Append(" ");
			this.GenerateCode(null, Node.IndexNode());
			this.CurrentBuilder.Append(") ");
		} else {
			this.CurrentBuilder.Append("nth ");
			this.GenerateCode(null, Node.IndexNode());
			this.CurrentBuilder.Append(" ");
			this.GenerateCode(null, Node.RecvNode());
		}
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitSetIndexNode(SetIndexNode Node) {
		this.CurrentBuilder.Append("(setf (nth ");
		this.GenerateCode(null, Node.IndexNode());
		this.CurrentBuilder.Append(" ");
		this.GenerateCode(null, Node.RecvNode());
		this.CurrentBuilder.Append(") ");
		this.GenerateCode(null, Node.ExprNode());
		this.CurrentBuilder.Append(")");
	}
}
