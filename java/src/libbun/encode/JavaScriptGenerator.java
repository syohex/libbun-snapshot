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
import libbun.ast.decl.BunClassNode;
import libbun.ast.decl.BunFunctionNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.ast.error.ErrorNode;
import libbun.ast.error.StupidCastErrorNode;
import libbun.ast.expression.NewObjectNode;
import libbun.ast.literal.BunMapLiteralNode;
import libbun.ast.literal.BunNullNode;
import libbun.ast.statement.BunThrowNode;
import libbun.ast.statement.BunTryNode;
import libbun.ast.unary.BunCastNode;
import libbun.parser.BLogger;
import libbun.type.BClassType;
import libbun.type.BFuncType;
import libbun.type.BType;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.ZenMethod;

public class JavaScriptGenerator extends OldSourceGenerator {
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
			this.Source.AppendNewLine("main();");
			this.Source.AppendLineFeed();
		}
	}

	@Override protected void GenerateImportLibrary(String LibName) {
		this.Header.AppendNewLine(LibName);
	}

	@Override public void VisitNewObjectNode(NewObjectNode Node) {
		this.Source.Append("new ", this.NameClass(Node.Type));
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitCastNode(BunCastNode Node) {
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitInstanceOfNode(BInstanceOfNode Node) {
		this.Source.Append("(");
		this.GenerateCode(null, Node.LeftNode());
		this.Source.Append(").constructor.name === ");
		this.GenerateTypeName(Node.TargetType());
		this.Source.Append(".name");
	}

	@Override public void VisitThrowNode(BunThrowNode Node) {
		this.Source.Append("throw ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitTryNode(BunTryNode Node) {
		this.Source.Append("try");
		this.GenerateCode(null, Node.TryBlockNode());
		if(Node.HasCatchBlockNode()){
			@Var String VarName = this.NameUniqueSymbol("e");
			this.Source.Append("catch(", VarName, ")");
			this.GenerateCode(null, Node.CatchBlockNode());
		}
		if (Node.HasFinallyBlockNode()) {
			this.Source.Append("finally");
			this.GenerateCode(null, Node.FinallyBlockNode());
		}
	}

	@Override
	protected void VisitVarDeclNode(BunLetVarNode Node) {
		this.Source.Append("var ");
		this.Source.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
		this.Source.Append(" = ");
		this.GenerateCode(null, Node.InitValueNode());
		this.Source.Append(this.SemiColon);
		if(Node.HasNextVarNode()) { this.VisitVarDeclNode(Node.NextVarNode()); }
	}

	@Override protected void VisitParamNode(BunLetVarNode Node) {
		this.Source.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
	}

	private void VisitAnonymousFunctionNode(BunFunctionNode Node) {
		this.VisitFuncParamNode("(function(", Node, ")");
		this.GenerateCode(null, Node.BlockNode());
		this.Source.Append(")");
	}

	@Override public void VisitFunctionNode(BunFunctionNode Node) {
		if(Node.FuncName() == null) {
			this.VisitAnonymousFunctionNode(Node);
			return;
		}
		@Var BFuncType FuncType = Node.GetFuncType();
		this.Source.Append("function ", Node.GetSignature());
		this.VisitFuncParamNode("(", Node, ")");
		this.GenerateCode(null, Node.BlockNode());
		if(Node.IsExport) {
			this.Source.Append(";");
			this.Source.AppendLineFeed();
			this.Source.Append(Node.FuncName(), " = ", FuncType.StringfySignature(Node.FuncName()));
			if(Node.FuncName().equals("main")) {
				this.HasMainFunction = true;
			}
		}
		if(this.IsMethod(Node.FuncName(), FuncType)) {
			this.Source.Append(";");
			this.Source.AppendLineFeed();
			this.Source.Append(this.NameClass(FuncType.GetRecvType()), ".prototype.", Node.FuncName());
			this.Source.Append(" = ", FuncType.StringfySignature(Node.FuncName()));
		}
	}

	@Override public void VisitMapLiteralNode(BunMapLiteralNode Node) {
		@Var int ListSize =  Node.GetListSize();
		@Var int i = 0;
		while(i < ListSize) {
			@Var BNode KeyNode = Node.GetListAt(i);
			if(KeyNode instanceof ErrorNode){
				this.GenerateCode(null, KeyNode);
				return;
			}
			i = i + 1;
		}
		this.Source.Append("{");
		while(i < ListSize) {
			@Var BNode KeyNode = Node.GetListAt(i);
			if (i > 0) {
				this.Source.Append(", ");
			}
			this.GenerateCode(null, KeyNode);
			this.Source.Append(": ");
			i = i + 1;
			if(i < Node.GetListSize()){
				@Var BNode ValueNode = Node.GetListAt(i);
				this.GenerateCode(null, ValueNode);
				i = i + 1;
			}else{
				this.Source.Append("null");
			}
		}
		this.Source.Append("}");
	}

	@Override public void VisitLetNode(BunLetVarNode Node) {
		this.Source.AppendNewLine("var ", Node.GetUniqueName(this), " = ");
		this.GenerateCode(null, Node.InitValueNode());
	}

	@Override public void VisitClassNode(BunClassNode Node) {
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
		this.Source.AppendNewLine("var ", ClassName, " = ");
		this.Source.Append("(function(");
		if(HasSuperType) {
			this.Source.OpenIndent("_super) {");
			this.Source.AppendNewLine("__extends(", ClassName, this.Camma);
			this.Source.Append("_super)", this.SemiColon);
		} else {
			this.Source.OpenIndent(") {");
		}
		this.Source.AppendNewLine("function ", ClassName);
		this.Source.OpenIndent("() {");
		if(HasSuperType) {
			this.Source.AppendNewLine("_super.call(this)", this.SemiColon);
		}

		@Var int i = 0;
		while (i < Node.GetListSize()) {
			@Var BunLetVarNode FieldNode = Node.GetFieldNode(i);
			@Var BNode ValueNode = FieldNode.InitValueNode();
			if(!(ValueNode instanceof BunNullNode)) {
				this.Source.AppendNewLine("this.");
				this.Source.Append(FieldNode.GetGivenName());
				this.Source.Append(" = ");
				this.GenerateCode(null, FieldNode.InitValueNode());
				this.Source.Append(this.SemiColon);
			}
			i = i + 1;
		}
		this.Source.CloseIndent("}");

		this.Source.AppendNewLine("return ", ClassName, this.SemiColon);
		this.Source.CloseIndent("})(");
		if(HasSuperType) {
			this.Source.Append(this.NameClass(Node.SuperType()));
		}
		this.Source.Append(")");
	}

	@Override public void VisitErrorNode(ErrorNode Node) {
		if(Node instanceof StupidCastErrorNode) {
			@Var StupidCastErrorNode ErrorNode = (StupidCastErrorNode)Node;
			this.GenerateCode(null, ErrorNode.ErrorNode);
		}
		else {
			@Var String Message = BLogger._LogError(Node.SourceToken, Node.ErrorMessage);
			this.Source.AppendWhiteSpace();
			this.Source.Append("throw new Error(");
			this.Source.Append(BLib._QuoteString(Message));
			this.Source.Append(")");
		}
	}

}
