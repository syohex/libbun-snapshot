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
import libbun.ast.expression.BFuncCallNode;
import libbun.ast.expression.BFuncNameNode;
import libbun.ast.expression.BMethodCallNode;
import libbun.ast.literal.BNullNode;
import libbun.ast.literal.ZMapLiteralNode;
import libbun.ast.statement.BThrowNode;
import libbun.ast.statement.BTryNode;
import libbun.ast.unary.BCastNode;
import libbun.parser.BLogger;
import libbun.type.BClassType;
import libbun.type.BGenericType;
import libbun.type.BType;
import libbun.util.BLib;
import libbun.util.Var;

public class JavaScriptGenerator extends ZSourceGenerator {
	private static boolean UseExtend;

	public JavaScriptGenerator() {
		super("js", "JavaScript-1.4");
		this.TopType = "Object";
		this.SetNativeType(BType.BooleanType, "Boolean");
		this.SetNativeType(BType.IntType, "Number");
		this.SetNativeType(BType.FloatType, "Number");
		this.SetNativeType(BType.StringType, "String");
		this.SetNativeType(BType.VarType, "Object");

		this.SetReservedName("this", "self");

		JavaScriptGenerator.UseExtend = false;
	}

	//	@Override public void VisitGlobalNameNode(ZFuncNameNode Node) {
	//		//		if(Node.IsUntyped()) {
	//		//			this.CurrentBuilder.Append(Node.GlobalName);
	//		//		}
	//		if(Node.IsFuncNameNode()) {
	//			if(Node.GlobalName.startsWith("LibZen")){
	//				this.CurrentBuilder.Append(Node.GlobalName.replace('_', '.'));
	//			}else{
	//				this.CurrentBuilder.Append(Node.Type.StringfySignature(Node.GlobalName));
	//			}
	//		}else{
	//			this.CurrentBuilder.Append(Node.GlobalName);
	//		}
	//	}

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

	//	@Override public void VisitCatchNode(ZCatchNode Node) {
	//		this.CurrentBuilder.Append("catch");
	//		this.CurrentBuilder.AppendWhiteSpace();
	//		this.CurrentBuilder.Append(Node.GivenName);
	//		this.GenerateCode(null, Node.AST[ZCatchNode._Block]);
	//	}

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

	private boolean IsUserDefinedType(BType SelfType){
		return SelfType != BType.BooleanType &&
				SelfType != BType.IntType &&
				SelfType != BType.FloatType &&
				SelfType != BType.StringType &&
				SelfType != BType.VoidType &&
				SelfType != BType.TypeType &&
				//SelfType != ZType.VarType &&
				SelfType.GetBaseType() != BGenericType._ArrayType &&
				SelfType.GetBaseType() != BGenericType._MapType;
	}

	@Override public void VisitFunctionNode(BFunctionNode Node) {
		@Var boolean IsLambda = (Node.FuncName() == null);
		@Var boolean IsInstanceMethod = (!IsLambda && Node.AST.length > 1 && Node.AST[1/*first param*/] instanceof BLetVarNode);
		@Var BType SelfType = IsInstanceMethod ? Node.AST[1/*first param*/].Type : null;
		@Var boolean IsConstructor = IsInstanceMethod && Node.FuncName().equals(SelfType.GetName());

		if(IsConstructor){
			@Var BNode Block = Node.BlockNode();
			Block.AST[Block.AST.length - 1].AST[0] = Node.AST[1];
		}
		if(IsLambda) {
			this.CurrentBuilder.Append("(function");
		}else{
			this.CurrentBuilder.Append("function ");
			if(!Node.Type.IsVoidType()) {
				@Var String FuncName = Node.GetUniqueName(this);
				this.CurrentBuilder.Append(FuncName);
			}
			else {
				this.CurrentBuilder.Append(Node.GetSignature());
			}
		}
		this.VisitFuncParamNode("(", Node, ")");
		this.GenerateCode(null, Node.BlockNode());
		if(IsLambda) {
			this.CurrentBuilder.Append(")");
		}else{
			this.CurrentBuilder.Append(this.SemiColon);
			if(IsInstanceMethod) {
				if(this.IsUserDefinedType(SelfType) && !IsConstructor){
					this.CurrentBuilder.AppendLineFeed();
					this.CurrentBuilder.Append(SelfType.ShortName); //FIXME must use typing in param
					this.CurrentBuilder.Append(".prototype.");
					this.CurrentBuilder.Append(Node.FuncName());
					this.CurrentBuilder.Append("__ = ");
					this.CurrentBuilder.Append(Node.GetSignature());

					this.CurrentBuilder.AppendLineFeed();
					this.CurrentBuilder.Append(SelfType.ShortName); //FIXME must use typing in param
					this.CurrentBuilder.Append(".prototype.");
					this.CurrentBuilder.Append(Node.FuncName());
					this.CurrentBuilder.Append(" = (function(){ Array.prototype.unshift.call(arguments, this); return this.");
					this.CurrentBuilder.Append(Node.FuncName());
					this.CurrentBuilder.Append("__.apply(this, arguments); })");

					this.CurrentBuilder.Append(this.SemiColon);
					this.CurrentBuilder.AppendLineFeed();
					this.CurrentBuilder.Append("function ");
					this.CurrentBuilder.Append(SelfType.ShortName); //FIXME must use typing in param
					this.CurrentBuilder.Append("_");
					this.CurrentBuilder.Append(Node.FuncName());
					this.VisitListNode("(", Node, ")");
					this.CurrentBuilder.Append("{ return ");
					this.CurrentBuilder.Append(Node.GetSignature());
					this.VisitListNode("(", Node, "); ");
					this.CurrentBuilder.Append("}");
				}
			}
			this.CurrentBuilder.AppendLineFeed();
			this.CurrentBuilder.AppendLineFeed();
		}
	}

	//	@Override public void VisitFuncCallNode(ZFuncCallNode Node) {
	//		//this.GenerateCode(null, Node.FuncNameNode());
	//		@Var ZType FuncType = Node.GetFuncType();
	//		if(FuncType != null){
	//			@Var ZType RecvType = Node.GetFuncType().GetRecvType();
	//			if(this.IsUserDefinedType(RecvType) && RecvType.ShortName != null && !RecvType.ShortName.equals(Node.GetStaticFuncName())){
	//				this.CurrentBuilder.Append("(");
	//				this.GenerateCode(null, Node.FunctionNode());
	//				if(Node.FunctionNode() instanceof ZGetterNode){
	//					this.CurrentBuilder.Append("__ || ");
	//				}else{
	//					this.CurrentBuilder.Append(" || ");
	//				}
	//				this.CurrentBuilder.Append(RecvType.ShortName);
	//				this.CurrentBuilder.Append("_");
	//				this.CurrentBuilder.Append(Node.GetStaticFuncName());
	//				this.CurrentBuilder.Append(")");
	//			}else{
	//				this.GenerateCode(null, Node.FunctionNode());
	//			}
	//		}else{
	//			this.GenerateCode(null, Node.FunctionNode());
	//		}
	//		this.VisitListNode("(", Node, ")");
	//	}

	@Override public void VisitFuncCallNode(BFuncCallNode Node) {
		@Var BFuncNameNode FuncNameNode = Node.FuncNameNode();
		if(FuncNameNode != null) {
			this.GenerateFuncName(FuncNameNode);
		}
		else {
			this.GenerateCode(null, Node.FunctorNode());
		}
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitMethodCallNode(BMethodCallNode Node) {
		// (recv.method || Type_method)(...)
		@Var BNode RecvNode = Node.RecvNode();

		if(this.IsUserDefinedType(RecvNode.Type)){
			this.CurrentBuilder.Append("(");
			this.GenerateSurroundCode(RecvNode);
			this.CurrentBuilder.Append(".");
			this.CurrentBuilder.Append(Node.MethodName());
			this.CurrentBuilder.Append("__ || ");
			this.CurrentBuilder.Append(RecvNode.Type.ShortName);
			this.CurrentBuilder.Append("_");
			this.CurrentBuilder.Append(Node.MethodName());
			this.CurrentBuilder.Append(")");
		}else{
			this.GenerateSurroundCode(RecvNode);
			this.CurrentBuilder.Append(".");
			this.CurrentBuilder.Append(Node.MethodName());
		}
		//this.GenerateSurroundCode(Node.RecvNode());
		this.VisitListNode("(", Node, ")");
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
		this.CurrentBuilder.Append(this.SemiColon);
	}

	private void GenerateExtendCode(BClassNode Node) {
		this.CurrentBuilder.Append("var __extends = this.__extends || function (d, b) {");
		this.CurrentBuilder.AppendLineFeed();
		this.CurrentBuilder.Indent();
		this.CurrentBuilder.AppendIndent();
		this.CurrentBuilder.Append("for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];");
		this.CurrentBuilder.AppendLineFeed();
		this.CurrentBuilder.AppendIndent();
		this.CurrentBuilder.Append("function __() { this.constructor = d; }");
		this.CurrentBuilder.AppendLineFeed();
		this.CurrentBuilder.AppendIndent();
		this.CurrentBuilder.Append("__.prototype = b.prototype;");
		this.CurrentBuilder.AppendLineFeed();
		this.CurrentBuilder.AppendIndent();
		this.CurrentBuilder.Append("d.prototype = new __();");
		this.CurrentBuilder.AppendLineFeed();
		this.CurrentBuilder.Append("};");
		this.CurrentBuilder.AppendLineFeed();
		this.CurrentBuilder.UnIndent();
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
		if(!Node.SuperType().Equals(BClassType._ObjectType) && !JavaScriptGenerator.UseExtend) {
			JavaScriptGenerator.UseExtend = true;
			this.GenerateExtendCode(Node);
		}
		this.CurrentBuilder.AppendNewLine("var ", Node.ClassName(), " = ");
		this.CurrentBuilder.Append("(function(");
		if(!Node.SuperType().Equals(BClassType._ObjectType)) {
			this.CurrentBuilder.OpenIndent("_super) {");
			this.CurrentBuilder.AppendNewLine("__extends(", Node.ClassName(), this.Camma);
			this.CurrentBuilder.Append("_super)", this.SemiColon);
		} else {
			this.CurrentBuilder.OpenIndent(") {");
		}
		this.CurrentBuilder.AppendNewLine("function ", Node.ClassName());
		this.CurrentBuilder.OpenIndent("() {");
		if(!Node.SuperType().Equals(BClassType._ObjectType)) {
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

		this.CurrentBuilder.AppendNewLine("return ", Node.ClassName(), this.SemiColon);
		this.CurrentBuilder.CloseIndent("})(");
		if(Node.SuperType() != null) {
			this.CurrentBuilder.Append(Node.SuperType().GetName());
		}
		this.CurrentBuilder.Append(")", this.SemiColon);
	}

	@Override public void VisitErrorNode(BErrorNode Node) {
		if(Node instanceof ZStupidCastErrorNode) {
			@Var ZStupidCastErrorNode ErrorNode = (ZStupidCastErrorNode)Node;
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

}
