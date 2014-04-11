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

import libbun.ast.BNode;
import libbun.ast.BunBlockNode;
import libbun.ast.GroupNode;
import libbun.ast.binary.BinaryOperatorNode;
import libbun.ast.binary.BunAndNode;
import libbun.ast.binary.BunOrNode;
import libbun.ast.binary.ComparatorNode;
import libbun.ast.decl.BunFunctionNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.ast.decl.BunVarBlockNode;
import libbun.ast.error.ErrorNode;
import libbun.ast.expression.BunFuncNameNode;
import libbun.ast.expression.FuncCallNode;
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
import libbun.encode.obsolete.OldSourceGenerator;
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
		this.Source.Append("(setq ");
		this.VisitGetNameNode(Node.NameNode());
		this.Source.Append(" ");
		this.GenerateExpression(Node.ExprNode());
		this.Source.Append(")");
	}

	@Override public void VisitUnaryNode(UnaryOperatorNode Node) {
		this.Source.Append("(");
		this.Source.Append(Node.SourceToken.GetText());
		this.Source.Append(" ");
		this.GenerateExpression(Node.RecvNode());
		this.Source.Append(")");
	}

	@Override protected void GenerateExpression(BNode Node) {
		this.GenerateExpression(Node);
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
		this.Source.Append("(");
		String operator = this.GetBinaryOperator(Node.SourceToken);
		if (operator.equals("/")) {
			this.Source.Append("floor" + " ");
			this.Source.Append("(");
		}
		this.Source.Append(operator);
		this.Source.Append(" ");
		this.GenerateExpression(Node.LeftNode());
		this.Source.Append(" ");
		this.GenerateExpression(Node.RightNode());
		if (operator.equals("/")) {
			this.Source.Append(")");
		}
		this.Source.Append(")");
	}

	@Override public void VisitComparatorNode(ComparatorNode Node) {
		this.Source.Append("(");
		String operator = this.GetBinaryOperator(Node.SourceToken);
		if (operator.equals("!=")) {
			this.Source.Append("not" + " ");
			this.Source.Append("(equal");
		} else {
			this.Source.Append(operator);
		}
		this.Source.Append(" ");
		this.GenerateExpression(Node.LeftNode());
		this.Source.Append(" ");
		this.GenerateExpression(Node.RightNode());
		if (operator.equals("!=")) {
			this.Source.Append(")");
		}
		this.Source.Append(")");
	}

	@Override public void VisitAndNode(BunAndNode Node) {
		this.Source.Append("(and ");
		this.GenerateExpression(Node.LeftNode());
		this.Source.Append(" ");
		this.GenerateExpression(Node.RightNode());
		this.Source.Append(")");
	}

	@Override public void VisitOrNode(BunOrNode Node) {
		this.Source.Append("(or ");
		this.GenerateExpression(Node.LeftNode());
		this.Source.Append(" ");
		this.GenerateExpression(Node.RightNode());
		this.Source.Append(")");
	}

	//
	// Visitor API
	//
	@Override public void VisitWhileNode(BunWhileNode Node) {
		this.Source.Append("(loop while ");
		this.GenerateExpression(Node.CondNode());
		this.Source.AppendNewLine();
		this.Source.Append("do");
		this.Source.AppendNewLine();
		this.GenerateExpression(Node.BlockNode());
		this.Source.Append(")");
	}

	@Override
	protected void VisitVarDeclNode(BunLetVarNode Node) {
		this.Source.Append("(", this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()), " ");
		this.GenerateExpression(Node.InitValueNode());
		this.Source.Append(")");
		if(Node.HasNextVarNode()) {
			this.VisitVarDeclNode(Node.NextVarNode());
		}
	}

	@Override
	protected void GenerateStmtListNode(BunBlockNode BlockNode) {
		@Var int Size = BlockNode.GetListSize();
		if(Size == 0) {
			this.Source.Append("()");
		}
		else if(Size == 1) {
			this.GenerateStatement(BlockNode.GetListAt(0));
		}
		else {
			this.Source.OpenIndent("(progn");
			@Var int i = 0;
			while (i < BlockNode.GetListSize()) {
				@Var BNode SubNode = BlockNode.GetListAt(i);
				this.GenerateStatement(SubNode);
				i = i + 1;
			}
			this.Source.CloseIndent(")");
		}
	}

	@Override public void VisitBlockNode(BunBlockNode Node) {
		this.GenerateStmtListNode(Node);
	}

	@Override public void VisitVarBlockNode(BunVarBlockNode Node) {
		this.Source.Append("(let (");
		this.VisitVarDeclNode(Node.VarDeclNode());
		this.Source.Append(")");
		this.GenerateStmtListNode(Node);
		this.Source.Append(")");
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
		this.Source.Append("(if  ");
		this.GenerateExpression(Node.CondNode());
		this.Source.Append(" ");
		this.GenerateExpression(Node.ThenNode());
		this.Source.Append(" ");
		if(Node.HasElseNode()) {
			this.GenerateExpression(Node.ElseNode());
		}
		else {
			this.Source.Append("nil");
		}
		this.Source.Append(")");
	}

	@Override public void VisitFuncCallNode(FuncCallNode Node) {
		this.Source.Append("(");
		@Var BunFuncNameNode FuncNameNode = Node.FuncNameNode();
		if(FuncNameNode != null) {
			this.GenerateFuncName(FuncNameNode);
		}
		else {
			this.Source.Append("funcall ");
			this.GenerateExpression(Node.FunctorNode());
		}
		this.GenerateListNode(" ", Node, " ");
		this.Source.Append(")");
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
			this.Source.Append("(return-from ", this.ClFunctionName(FuncNode), " ");
		}
		else {
			this.Source.Append("(return ");
		}
		if(Node.HasReturnExpr()) {
			this.GenerateExpression(Node.ExprNode());
		}
		else {
			this.Source.Append("nil");
		}
		this.Source.Append(")");
	}

	@Override protected void VisitParamNode(BunLetVarNode Node) {
		this.Source.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
	}

	@Override public void VisitFunctionNode(BunFunctionNode Node) {
		if(!Node.IsTopLevelDefineFunction()) {
			this.Source.Append("#'(lambda ");
			this.VisitFuncParamNode("(", Node, ")");
			this.Source.Append("(block nil ");
			this.GenerateExpression(Node.BlockNode());
			this.Source.Append("))");
		}
		else {
			@Var BFuncType FuncType = Node.GetFuncType();
			this.Source.Append("(defun ");
			this.Source.Append(this.ClFunctionName(Node));
			this.VisitFuncParamNode(" (", Node, ")");
			this.GenerateExpression(Node.BlockNode());
			this.Source.Append(")");
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
		this.Source.Append("(error ");
		this.Source.Append(Node.ErrorMessage);
		this.Source.Append(")");
	}

	@Override public void VisitTryNode(BunTryNode Node) {
		this.Source.Append("(unwind-protect ");
		this.Source.Append("(handler-case ");
		this.GenerateExpression(Node.TryBlockNode());
		if(Node.HasCatchBlockNode()) {
			@Var String VarName = this.NameUniqueSymbol("e");
			this.Source.AppendNewLine("(error (", VarName, ")");
			this.GenerateStmtListNode(Node.CatchBlockNode());
			this.Source.AppendNewLine(")");
		}
		this.Source.Append(")");
		if(Node.HasFinallyBlockNode()) {
			this.GenerateExpression(Node.FinallyBlockNode());
		}
		this.Source.Append(")");
	}


	@Override @ZenMethod protected void Finish(String FileName) {
		if(this.hasMain) {
			this.Source.AppendNewLine("(main)");
			this.Source.AppendLineFeed();
		}
	}

	@Override public void VisitGroupNode(GroupNode Node) {
		this.GenerateExpression("", Node.ExprNode(), "");
	}

	@Override public void VisitNotNode(BunNotNode Node) {
		this.Source.Append("(", this.NotOperator, " ");
		this.GenerateExpression(Node.RecvNode());
		this.Source.Append(") ");
	}

	@Override public void VisitLetNode(BunLetVarNode Node) {
		this.Source.AppendNewLine("(setf ", Node.GetUniqueName(this), "");
		this.Source.Append(" ");
		this.GenerateExpression(Node.InitValueNode());
		this.Source.Append(")");
	}

	@Override public void VisitThrowNode(BunThrowNode Node) {
		this.Source.Append("(throw nil ");
		this.GenerateExpression(Node.ExprNode());
		this.Source.Append(")");
	}

	@Override public void VisitBreakNode(BunBreakNode Node) {
		this.Source.Append("(return)");
	}

	@Override public void VisitArrayLiteralNode(BunArrayLiteralNode Node) {
		this.GenerateListNode("'(", Node, ")");
	}

	@Override public void VisitGetIndexNode(GetIndexNode Node) {
		this.Source.Append("(");
		if (Node.RecvNode().Type == BType.StringType) {
			this.Source.Append("string (");
			this.Source.Append("aref ");
			this.GenerateExpression(Node.RecvNode());
			this.Source.Append(" ");
			this.GenerateExpression(Node.IndexNode());
			this.Source.Append(") ");
		} else {
			this.Source.Append("nth ");
			this.GenerateExpression(Node.IndexNode());
			this.Source.Append(" ");
			this.GenerateExpression(Node.RecvNode());
		}
		this.Source.Append(")");
	}

	@Override public void VisitSetIndexNode(SetIndexNode Node) {
		this.Source.Append("(setf (nth ");
		this.GenerateExpression(Node.IndexNode());
		this.Source.Append(" ");
		this.GenerateExpression(Node.RecvNode());
		this.Source.Append(") ");
		this.GenerateExpression(Node.ExprNode());
		this.Source.Append(")");
	}
}
