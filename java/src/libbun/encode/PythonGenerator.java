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
import libbun.ast.BNode;
import libbun.ast.binary.BInstanceOfNode;
import libbun.ast.decl.BunClassNode;
import libbun.ast.decl.BunFunctionNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.ast.decl.ZVarBlockNode;
import libbun.ast.error.ErrorNode;
import libbun.ast.error.StupidCastErrorNode;
import libbun.ast.expression.GetIndexNode;
import libbun.ast.expression.NewObjectNode;
import libbun.ast.statement.BunIfNode;
import libbun.ast.statement.BunThrowNode;
import libbun.ast.statement.BunTryNode;
import libbun.ast.unary.BunCastNode;
import libbun.parser.BLogger;
import libbun.type.BClassField;
import libbun.type.BClassType;
import libbun.type.BFuncType;
import libbun.type.BType;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.ZenMethod;

//Zen Generator should be written in each language.

public class PythonGenerator extends ZSourceGenerator {

	@BField boolean HasMainFunction = false;

	public PythonGenerator() {
		super("py", "Python-2.7.1");
		this.LineComment = "#"; // if not, set null
		this.BeginComment = null; //"'''";
		this.EndComment = null; //"'''";
		this.Camma = ", ";
		this.SemiColon = "";
		this.StringLiteralPrefix = "";

		this.TrueLiteral = "True";
		this.FalseLiteral = "False";
		this.NullLiteral = "None";

		this.AndOperator = "and";
		this.OrOperator = "or";
		this.NotOperator = "not ";

		this.TopType = "object";
		this.SetNativeType(BType.BooleanType, "bool");
		this.SetNativeType(BType.IntType, "int");
		this.SetNativeType(BType.FloatType, "float");
		this.SetNativeType(BType.StringType, "str");

		this.HeaderBuilder.Append("#! /usr/bin/env python");
		this.HeaderBuilder.AppendNewLine("# -*- coding: utf-8 -*-");
		this.HeaderBuilder.AppendNewLine("def zstr(s) : return str(s) if s != None else \'null\'");
		this.CurrentBuilder.AppendNewLine("## end of header", this.LineFeed);

	}

	@Override protected void GenerateImportLibrary(String LibName) {
		if(LibName.startsWith("def ")) {
			this.HeaderBuilder.AppendNewLine(LibName);
		}
		else {
			this.HeaderBuilder.AppendNewLine("import ", LibName);
		}
	}

	@Override @ZenMethod protected void Finish(String FileName) {
		if(this.HasMainFunction) {
			this.CurrentBuilder.AppendNewLine("if __name__ == \"__main__\":\n\tmain()");
			this.CurrentBuilder.AppendLineFeed();
		}
	}

	@Override
	public void VisitStmtList(BunBlockNode BlockNode) {
		@Var int i = 0;
		while (i < BlockNode.GetListSize()) {
			BNode SubNode = BlockNode.GetListAt(i);
			this.GenerateStatement(SubNode);
			i = i + 1;
		}
		if (i == 0) {
			this.CurrentBuilder.AppendNewLine("pass");
		}
	}

	@Override public void VisitBlockNode(BunBlockNode Node) {
		this.CurrentBuilder.OpenIndent(":");
		this.VisitStmtList(Node);
		this.CurrentBuilder.CloseIndent("");
	}

	@Override protected void VisitVarDeclNode(BunLetVarNode Node) {
		this.CurrentBuilder.AppendNewLine(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()), " = ");
		this.GenerateCode(null, Node.InitValueNode());
		if(Node.HasNextVarNode()) {
			this.VisitVarDeclNode(Node.NextVarNode());
		}
	}

	@Override public void VisitVarBlockNode(ZVarBlockNode Node) {
		this.VisitVarDeclNode(Node.VarDeclNode());
		this.VisitStmtList(Node);
	}

	@Override public void VisitNewObjectNode(NewObjectNode Node) {
		this.CurrentBuilder.Append(this.NameClass(Node.Type));
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitCastNode(BunCastNode Node) {
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitGetIndexNode(GetIndexNode Node) {
		@Var BType RecvType = Node.GetAstType(GetIndexNode._Recv);
		if(RecvType.IsMapType()) {
			this.ImportLibrary("def zGetMap(m,k): return m[k] if m.has_key(k) else None");
			this.GenerateCode2("zGetMap(", null, Node.RecvNode(), ", ");
			this.GenerateCode2("", null, Node.IndexNode(), ")");
		}
		else {
			this.GenerateCode(null, Node.RecvNode());
			this.GenerateCode2("[", null, Node.IndexNode(), "]");
		}
	}

	@Override public void VisitInstanceOfNode(BInstanceOfNode Node) {
		this.CurrentBuilder.Append("isinstance(");
		this.GenerateCode(null, Node.LeftNode());
		if(Node.TargetType() instanceof BClassType) {
			this.CurrentBuilder.Append(this.Camma, this.NameClass(Node.TargetType()), ")");
		}
		else {
			this.CurrentBuilder.Append(this.Camma);
			this.GenerateTypeName(Node.TargetType());
			this.CurrentBuilder.Append(")");
		}
	}

	@Override public void VisitIfNode(BunIfNode Node) {
		this.CurrentBuilder.Append("if ");
		this.GenerateCode(null, Node.CondNode());
		this.GenerateCode(null, Node.ThenNode());
		if (Node.HasElseNode()) {
			BNode ElseNode = Node.ElseNode();
			if(ElseNode instanceof BunIfNode) {
				this.CurrentBuilder.AppendNewLine("el");
			}
			else {
				this.CurrentBuilder.AppendNewLine("else");
			}
			this.GenerateCode(null, Node.ElseNode());
		}
	}

	@Override public void VisitLetNode(BunLetVarNode Node) {
		if(this.ReadableCode || !Node.IsConstValue()) {
			this.CurrentBuilder.Append(Node.GetUniqueName(this));
			this.CurrentBuilder.Append(" = ");
			this.GenerateCode(null, Node.InitValueNode());
		}
	}

	@Override protected void VisitParamNode(BunLetVarNode Node) {
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
	}

	/**
	>>> def f(x):
		...   def g(y):
		...     return x + y
		...   return g
		...
		>>> f(1)(3)
		4
	 **/

	@Override public void VisitFunctionNode(BunFunctionNode Node) {
		if(!Node.IsTopLevelDefineFunction()) {
			@Var String FuncName = Node.GetUniqueName(this);
			this.CurrentBuilder = this.InsertNewSourceBuilder();
			this.CurrentBuilder.Append("def ");
			this.CurrentBuilder.Append(FuncName);
			this.VisitFuncParamNode("(", Node, ")");
			this.GenerateCode(null, Node.BlockNode());
			this.CurrentBuilder.AppendLineFeed();
			this.CurrentBuilder.AppendLineFeed();
			this.CurrentBuilder = this.CurrentBuilder.Pop();
			this.CurrentBuilder.Append(FuncName);
		}
		else {
			@Var BFuncType FuncType = Node.GetFuncType();
			this.CurrentBuilder.Append("def ");
			this.CurrentBuilder.Append(Node.GetSignature());
			this.VisitFuncParamNode("(", Node, ")");
			this.GenerateCode(null, Node.BlockNode());
			this.CurrentBuilder.AppendLineFeed();
			if(Node.IsExport) {
				this.CurrentBuilder.Append(Node.FuncName(), " = ", FuncType.StringfySignature(Node.FuncName()));
				this.CurrentBuilder.AppendLineFeed();
				if(Node.FuncName().equals("main")) {
					this.HasMainFunction = true;
				}
			}
			if(this.IsMethod(Node.FuncName(), FuncType)) {
				this.CurrentBuilder.Append(this.NameMethod(FuncType.GetRecvType(), Node.FuncName()));
				this.CurrentBuilder.Append(" = ", FuncType.StringfySignature(Node.FuncName()));
				this.CurrentBuilder.AppendLineFeed();
			}
		}
	}

	private void GenerateMethodVariables(BunClassNode Node) {
		@Var int i = 0;
		while (i < Node.ClassType.GetFieldSize()) {
			@Var BClassField ClassField = Node.ClassType.GetFieldAt(i);
			if(ClassField.FieldType.IsFuncType()) {
				this.CurrentBuilder.AppendNewLine();
				this.CurrentBuilder.Append(this.NameMethod(Node.ClassType, ClassField.FieldName));
				this.CurrentBuilder.Append(" = ", this.NullLiteral);
			}
			i = i + 1;
		}
		this.CurrentBuilder.AppendNewLine();
	}

	@Override public void VisitClassNode(BunClassNode Node) {
		@Var BType SuperType = Node.ClassType.GetSuperType();
		this.GenerateMethodVariables(Node);
		this.CurrentBuilder.Append("class ");
		this.CurrentBuilder.Append(this.NameClass(Node.ClassType));
		if(!SuperType.Equals(BClassType._ObjectType)) {
			this.CurrentBuilder.Append("(");
			this.CurrentBuilder.Append(this.NameClass(SuperType));
			this.CurrentBuilder.Append(")");
		}
		this.CurrentBuilder.Append(":");
		this.CurrentBuilder.Indent();
		this.CurrentBuilder.AppendNewLine();
		this.CurrentBuilder.Append("def __init__(self):");
		this.CurrentBuilder.Indent();
		if(!Node.SuperType().Equals(BClassType._ObjectType)) {
			this.CurrentBuilder.AppendNewLine();
			this.CurrentBuilder.Append(this.NameClass(SuperType));
			this.CurrentBuilder.Append(".__init__(self)");
		}
		@Var int i = 0;
		while (i < Node.GetListSize()) {
			@Var BunLetVarNode FieldNode = Node.GetFieldNode(i);
			if(!FieldNode.DeclType().IsFuncType()) {
				this.CurrentBuilder.AppendNewLine();
				this.CurrentBuilder.Append("self." + FieldNode.GetGivenName() + " = ");
				this.GenerateCode(null, FieldNode.InitValueNode());
			}
			this.CurrentBuilder.Append(this.SemiColon);
			i = i + 1;
		}

		i = 0;
		while (i < Node.ClassType.GetFieldSize()) {
			@Var BClassField ClassField = Node.ClassType.GetFieldAt(i);
			if(ClassField.FieldType.IsFuncType()) {
				this.CurrentBuilder.AppendNewLine();
				this.CurrentBuilder.Append("self." + ClassField.FieldName);
				this.CurrentBuilder.Append(" = _" + this.NameClass(Node.ClassType) + "_" + ClassField.FieldName);
			}
			i = i + 1;
		}
		this.CurrentBuilder.UnIndent();
		this.CurrentBuilder.UnIndent();
		this.CurrentBuilder.AppendLineFeed();
		this.CurrentBuilder.AppendLineFeed();
	}

	@Override public void VisitErrorNode(ErrorNode Node) {
		if(Node instanceof StupidCastErrorNode) {
			@Var StupidCastErrorNode ErrorNode = (StupidCastErrorNode)Node;
			this.GenerateCode(null, ErrorNode.ErrorNode);
		}
		else {
			@Var String Message = BLogger._LogError(Node.SourceToken, Node.ErrorMessage);
			this.CurrentBuilder.AppendWhiteSpace();
			this.CurrentBuilder.Append("LibZen.ThrowError(");
			this.CurrentBuilder.Append(BLib._QuoteString(Message));
			this.CurrentBuilder.Append(")");
		}
	}

	@Override public void VisitThrowNode(BunThrowNode Node) {
		this.CurrentBuilder.Append("raise ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitTryNode(BunTryNode Node) {
		this.CurrentBuilder.Append("try");
		this.GenerateCode(null, Node.TryBlockNode());
		if(Node.HasCatchBlockNode()) {
			@Var String VarName = this.NameUniqueSymbol("e");
			this.CurrentBuilder.AppendNewLine("except Exception as ", VarName);
			this.CurrentBuilder.OpenIndent(":");
			this.CurrentBuilder.AppendNewLine(Node.ExceptionName(), " = ", VarName);
			this.VisitStmtList(Node.CatchBlockNode());
			this.CurrentBuilder.CloseIndent("");
		}
		if(Node.HasFinallyBlockNode()) {
			this.CurrentBuilder.AppendNewLine("finally");
			this.GenerateCode(null, Node.FinallyBlockNode());
		}
	}

}