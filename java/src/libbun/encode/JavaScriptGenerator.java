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
import libbun.ast.binary.BInstanceOfNode;
import libbun.ast.decl.BClassNode;
import libbun.ast.decl.BFunctionNode;
import libbun.ast.decl.BLetVarNode;
import libbun.ast.error.BErrorNode;
import libbun.ast.error.ZStupidCastErrorNode;
import libbun.ast.expression.BNewObjectNode;
import libbun.ast.literal.BNullNode;
import libbun.ast.literal.ZMapLiteralNode;
import libbun.ast.statement.BThrowNode;
import libbun.ast.statement.BTryNode;
import libbun.ast.unary.BCastNode;
import libbun.parser.BLogger;
import libbun.type.BClassType;
import libbun.type.BFuncType;
import libbun.type.BType;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.ZenMethod;

public class JavaScriptGenerator extends ZSourceGenerator {
	@BField private boolean HasMainFunction;

	private static String ExtendCode =
			"var __extends = this.__extends || function (d, b) {\n"
					+ "\tfor (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];\n"
					+ "\tfunction __() { this.constructor = d; }"
					+ "\t__.prototype = b.prototype;\n"
					+ "\td.prototype = new __();\n"
					+ "}";

	public JavaScriptGenerator() {
		super("js", "JavaScript-1.4");
		this.TopType = "Object";
		this.SetNativeType(BType.BooleanType, "Boolean");
		this.SetNativeType(BType.IntType, "Number");
		this.SetNativeType(BType.FloatType, "Number");
		this.SetNativeType(BType.StringType, "String");
		this.SetNativeType(BType.VarType, "Object");

		this.SetReservedName("this", "self");
	}

	@Override @ZenMethod protected void Finish(String FileName) {
		if(this.HasMainFunction) {
			this.CurrentBuilder.AppendNewLine("main();");
			this.CurrentBuilder.AppendLineFeed();
		}
	}

	@Override protected void GenerateImportLibrary(String LibName) {
		this.HeaderBuilder.AppendNewLine(LibName);
	}

	@Override public void VisitNewObjectNode(BNewObjectNode Node) {
		this.CurrentBuilder.Append(this.NameClass(Node.Type));
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitCastNode(BCastNode Node) {
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitInstanceOfNode(BInstanceOfNode Node) {
		this.CurrentBuilder.Append("(");
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.Append(").constructor.name === ");
		this.GenerateTypeName(Node.TargetType());
		this.CurrentBuilder.Append(".name");
	}

	@Override public void VisitThrowNode(BThrowNode Node) {
		this.CurrentBuilder.Append("throw ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitTryNode(BTryNode Node) {
		this.CurrentBuilder.Append("try");
		this.GenerateCode(null, Node.TryBlockNode());
		if(Node.HasCatchBlockNode()){
			@Var String VarName = this.NameUniqueSymbol("e");
			this.CurrentBuilder.Append("catch(", VarName, ")");
			this.GenerateCode(null, Node.CatchBlockNode());
		}
		if (Node.HasFinallyBlockNode()) {
			this.CurrentBuilder.Append("finally");
			this.GenerateCode(null, Node.FinallyBlockNode());
		}
	}

	@Override
	protected void VisitVarDeclNode(BLetVarNode Node) {
		this.CurrentBuilder.AppendToken("var");
		this.CurrentBuilder.AppendWhiteSpace();
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
		this.CurrentBuilder.AppendToken("=");
		this.GenerateCode(null, Node.InitValueNode());
		this.CurrentBuilder.Append(this.SemiColon);
		if(Node.HasNextVarNode()) { this.VisitVarDeclNode(Node.NextVarNode()); }
	}

	@Override protected void VisitParamNode(BLetVarNode Node) {
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
	}

	private void VisitAnonymousFunctionNode(BFunctionNode Node) {
		this.VisitFuncParamNode("(function(", Node, ")");
		this.GenerateCode(null, Node.BlockNode());
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitFunctionNode(BFunctionNode Node) {
		if(Node.FuncName() == null) {
			this.VisitAnonymousFunctionNode(Node);
			return;
		}
		@Var BFuncType FuncType = Node.GetFuncType();
		this.CurrentBuilder.Append("function ", Node.GetSignature());
		this.VisitFuncParamNode("(", Node, ")");
		this.GenerateCode(null, Node.BlockNode());
		if(Node.IsExport) {
			this.CurrentBuilder.Append(";");
			this.CurrentBuilder.AppendLineFeed();
			this.CurrentBuilder.Append(Node.FuncName(), " = ", FuncType.StringfySignature(Node.FuncName()));
			if(Node.FuncName().equals("main")) {
				this.HasMainFunction = true;
			}
		}
		if(this.IsMethod(Node.FuncName(), FuncType)) {
			this.CurrentBuilder.Append(";");
			this.CurrentBuilder.AppendLineFeed();
			this.CurrentBuilder.Append(this.NameClass(FuncType.GetRecvType()), ".prototype.", Node.FuncName());
			this.CurrentBuilder.Append(" = ", FuncType.StringfySignature(Node.FuncName()));
		}
	}

	@Override public void VisitMapLiteralNode(ZMapLiteralNode Node) {
		@Var int ListSize =  Node.GetListSize();
		@Var int i = 0;
		while(i < ListSize) {
			@Var BNode KeyNode = Node.GetListAt(i);
			if(KeyNode instanceof BErrorNode){
				this.GenerateCode(null, KeyNode);
				return;
			}
			i = i + 1;
		}
		this.CurrentBuilder.Append("{");
		while(i < ListSize) {
			@Var BNode KeyNode = Node.GetListAt(i);
			if (i > 0) {
				this.CurrentBuilder.Append(", ");
			}
			this.GenerateCode(null, KeyNode);
			this.CurrentBuilder.Append(": ");
			i = i + 1;
			if(i < Node.GetListSize()){
				@Var BNode ValueNode = Node.GetListAt(i);
				this.GenerateCode(null, ValueNode);
				i = i + 1;
			}else{
				this.CurrentBuilder.Append("null");
			}
		}
		this.CurrentBuilder.Append("}");
	}

	@Override public void VisitLetNode(BLetVarNode Node) {
		this.CurrentBuilder.AppendNewLine("var ", Node.GetUniqueName(this), " = ");
		this.GenerateCode(null, Node.InitValueNode());
	}

	@Override public void VisitClassNode(BClassNode Node) {
		/* var ClassName = (function(_super) {
		 *  __extends(ClassName, _super);
		 * 	function ClassName(params) {
		 * 		_super.call(this, params);
		 * 	}
		 * 	ClassName.prototype.MethodName = function(){ };
		 * 	return ClassName;
		 * })(_super);
		 */
		@Var boolean HasSuperType = Node.SuperType() != null && !Node.SuperType().Equals(BClassType._ObjectType);
		@Var String ClassName = this.NameClass(Node.ClassType);
		if(HasSuperType) {
			this.ImportLibrary(JavaScriptGenerator.ExtendCode);
		}
		this.CurrentBuilder.AppendNewLine("var ", ClassName, " = ");
		this.CurrentBuilder.Append("(function(");
		if(HasSuperType) {
			this.CurrentBuilder.OpenIndent("_super) {");
			this.CurrentBuilder.AppendNewLine("__extends(", ClassName, this.Camma);
			this.CurrentBuilder.Append("_super)", this.SemiColon);
		} else {
			this.CurrentBuilder.OpenIndent(") {");
		}
		this.CurrentBuilder.AppendNewLine("function ", ClassName);
		this.CurrentBuilder.OpenIndent("() {");
		if(HasSuperType) {
			this.CurrentBuilder.AppendNewLine("_super.call(this)", this.SemiColon);
		}

		@Var int i = 0;
		while (i < Node.GetListSize()) {
			@Var BLetVarNode FieldNode = Node.GetFieldNode(i);
			@Var BNode ValueNode = FieldNode.InitValueNode();
			if(!(ValueNode instanceof BNullNode)) {
				this.CurrentBuilder.AppendNewLine("this.");
				this.CurrentBuilder.Append(FieldNode.GetGivenName());
				this.CurrentBuilder.Append(" = ");
				this.GenerateCode(null, FieldNode.InitValueNode());
				this.CurrentBuilder.Append(this.SemiColon);
			}
			i = i + 1;
		}
		this.CurrentBuilder.CloseIndent("}");

		this.CurrentBuilder.AppendNewLine("return ", ClassName, this.SemiColon);
		this.CurrentBuilder.CloseIndent("})(");
		if(HasSuperType) {
			this.CurrentBuilder.Append(this.NameClass(Node.SuperType()));
		}
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitErrorNode(BErrorNode Node) {
		if(Node instanceof ZStupidCastErrorNode) {
			@Var ZStupidCastErrorNode ErrorNode = (ZStupidCastErrorNode)Node;
			this.GenerateCode(null, ErrorNode.ErrorNode);
		}
		else {
			@Var String Message = BLogger._LogError(Node.SourceToken, Node.ErrorMessage);
			this.CurrentBuilder.AppendWhiteSpace();
			this.CurrentBuilder.Append("throw new Error(");
			this.CurrentBuilder.Append(BLib._QuoteString(Message));
			this.CurrentBuilder.Append(")");
		}
	}

}
