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
import libbun.ast.expression.FuncCallNode;
import libbun.ast.expression.BunFuncNameNode;
import libbun.ast.expression.GetIndexNode;
import libbun.ast.expression.GetFieldNode;
import libbun.ast.expression.MethodCallNode;
import libbun.ast.expression.NewObjectNode;
import libbun.ast.expression.SetIndexNode;
import libbun.ast.expression.SetFieldNode;
import libbun.ast.literal.BunArrayLiteralNode;
import libbun.ast.literal.BunMapLiteralNode;
import libbun.ast.statement.BunThrowNode;
import libbun.ast.statement.BunTryNode;
import libbun.parser.BLogger;
import libbun.type.BClassField;
import libbun.type.BClassType;
import libbun.type.BFunc;
import libbun.type.BFuncType;
import libbun.type.BType;
import libbun.util.Var;

public class CGenerator extends OldSourceGenerator {

	public CGenerator() {
		super("c", "C99");
		this.TrueLiteral  = "1/*true*/";
		this.FalseLiteral = "0/*false*/";
		this.NullLiteral  = "NULL";

		this.TopType = "ZObject *";
		this.SetNativeType(BType.BooleanType, "int");
		//		this.SetNativeType(ZType.IntType, "long long int");
		this.SetNativeType(BType.IntType, "long");
		this.SetNativeType(BType.FloatType, "double");
		this.SetNativeType(BType.StringType, "const char *");

		this.HeaderBuilder.AppendNewLine("/* end of header */", this.LineFeed);
	}

	@Override protected void GenerateImportLibrary(String LibName) {
		this.HeaderBuilder.AppendNewLine("#include<", LibName, ">");
	}

	@Override protected void GenerateCode(BType ContextType, BNode Node) {
		if(Node.IsUntyped() && !Node.IsErrorNode() && !(Node instanceof BunFuncNameNode)) {
			this.CurrentBuilder.Append("/*untyped*/" + this.NullLiteral);
			BLogger._LogError(Node.SourceToken, "untyped error: " + Node);
		}
		else {
			if(ContextType != null && Node.Type != ContextType) {
				this.CurrentBuilder.Append("(");
				this.GenerateTypeName(ContextType);
				this.CurrentBuilder.Append(")");
			}
			Node.Accept(this);
		}
	}

	@Override public void VisitArrayLiteralNode(BunArrayLiteralNode Node) {
		@Var BType ParamType = Node.Type.GetParamType(0);
		if(ParamType.IsIntType() || ParamType.IsBooleanType()) {
			this.CurrentBuilder.Append("LibZen_NewIntArray(");
		}
		else if(ParamType.IsFloatType()) {
			this.CurrentBuilder.Append("LibZen_NewFloatArray(");
		}
		else if(ParamType.IsStringType()) {
			this.CurrentBuilder.Append("LibZen_NewStringArray(");
		}
		else {
			this.CurrentBuilder.Append("LibZen_NewArray(");
		}
		this.CurrentBuilder.Append(String.valueOf(Node.GetListSize()));
		if(Node.GetListSize() > 0) {
			this.CurrentBuilder.Append(this.Camma);
		}
		this.VisitListNode("", Node, ")");
	}

	@Override public void VisitMapLiteralNode(BunMapLiteralNode Node) {
		@Var BType ParamType = Node.Type.GetParamType(0);
		if(ParamType.IsIntType() || ParamType.IsBooleanType()) {
			this.CurrentBuilder.Append("LibZen_NewIntMap(");
		}
		else if(ParamType.IsFloatType()) {
			this.CurrentBuilder.Append("LibZen_NewFloatMap(");
		}
		else if(ParamType.IsStringType()) {
			this.CurrentBuilder.Append("LibZen_NewStringMap(");
		}
		else {
			this.CurrentBuilder.Append("LibZen_NewMap(");
		}
		this.CurrentBuilder.Append(String.valueOf(Node.GetListSize()));
		if(Node.GetListSize() > 0) {
			this.CurrentBuilder.Append(this.Camma);
		}
		this.VisitListNode("", Node, ")");
	}

	@Override public void VisitNewObjectNode(NewObjectNode Node) {
		this.CurrentBuilder.Append("_New"+this.NameClass(Node.Type));
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitGetIndexNode(GetIndexNode Node) {
		this.CurrentBuilder.Append(this.NameType(Node.GetAstType(GetIndexNode._Recv)) + "GetIndex");
		this.CurrentBuilder.Append("(");
		this.GenerateCode(null, Node.IndexNode());
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitSetIndexNode(SetIndexNode Node) {
		this.CurrentBuilder.Append(this.NameType(Node.GetAstType(GetIndexNode._Recv)) + "SetIndex");
		this.CurrentBuilder.Append("(");
		this.GenerateCode(null, Node.IndexNode());
		this.CurrentBuilder.Append(this.Camma);
		this.GenerateCode(null, Node.ExprNode());
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitGetFieldNode(GetFieldNode Node) {
		this.GenerateSurroundCode(Node.RecvNode());
		this.CurrentBuilder.Append("->");
		this.CurrentBuilder.Append(Node.GetName());
	}

	@Override public void VisitSetFieldNode(SetFieldNode Node) {
		this.GenerateSurroundCode(Node.RecvNode());
		this.CurrentBuilder.Append("->");
		this.CurrentBuilder.Append(Node.GetName());
		this.CurrentBuilder.AppendToken("=");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitMethodCallNode(MethodCallNode Node) {
		//		this.GenerateSurroundCode(Node.RecvNode());
		//		this.CurrentBuilder.Append(".");
		//		this.CurrentBuilder.Append(Node.MethodName());
		//		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitFuncCallNode(FuncCallNode Node) {
		@Var BunFuncNameNode FuncNameNode = Node.FuncNameNode();
		if(FuncNameNode != null) {
			this.GenerateFuncName(FuncNameNode);
		}
		else {
			this.GenerateCode(null, Node.FunctorNode());
		}
		this.VisitListNode("(", Node, ")");
	}

	//	@Override public void VisitCastNode(ZCastNode Node) {
	//		this.CurrentBuilder.Append("(");
	//		this.VisitType(Node.Type);
	//		this.CurrentBuilder.Append(")");
	//		this.GenerateSurroundCode(Node.ExprNode());
	//	}

	@Override public void VisitThrowNode(BunThrowNode Node) {
		this.GenerateCode(null, Node.ExprNode());
		this.CurrentBuilder.Append("longjump(1)"); // FIXME
		this.CurrentBuilder.AppendWhiteSpace();
	}

	@Override public void VisitTryNode(BunTryNode Node) {
		//		this.CurrentBuilder.Append("try");
		//		this.GenerateCode(Node.TryNode());
		//		if(Node.CatchNode() != null) {
		//			this.GenerateCode(Node.CatchNode());
		//		}
		//		if (Node.FinallyNode() != null) {
		//			this.CurrentBuilder.Append("finally");
		//			this.GenerateCode(Node.FinallyNode());
		//		}
	}

	private String ParamTypeName(BType Type) {
		if(Type.IsArrayType()) {
			return "ArrayOf" + this.ParamTypeName(Type.GetParamType(0));
		}
		if(Type.IsMapType()) {
			return "MapOf" + this.ParamTypeName(Type.GetParamType(0));
		}
		if(Type.IsFuncType()) {
			@Var String s = "FuncOf";
			@Var int i = 0;
			while(i < Type.GetParamSize()) {
				s = s +  this.ParamTypeName(Type.GetParamType(i));
				i = i + 1;
			}
			return s + "_";
		}
		if(Type.IsIntType()) {
			return "Int";
		}
		if(Type.IsFloatType()) {
			return "Float";
		}
		if(Type.IsVoidType()) {
			return "Void";
		}
		if(Type.IsVarType()) {
			return "Var";
		}
		return Type.ShortName;
	}

	private String GetCTypeName(BType Type) {
		@Var String TypeName = null;
		if(Type.IsArrayType() || Type.IsMapType()) {
			TypeName = this.ParamTypeName(Type) + " *";
		}
		if(Type instanceof BClassType) {
			TypeName = "struct " + this.NameClass(Type) + " *";
		}
		if(TypeName == null) {
			TypeName = this.GetNativeTypeName(Type);
		}
		return TypeName;
	}

	protected void GenerateFuncTypeName(BType Type, String FuncName) {
		this.GenerateTypeName(Type.GetParamType(0));
		this.CurrentBuilder.Append(" (*" + FuncName + ")");
		@Var int i = 1;
		this.CurrentBuilder.Append("(");
		while(i < Type.GetParamSize()) {
			if(i > 1) {
				this.CurrentBuilder.Append(",");
			}
			this.GenerateTypeName(Type.GetParamType(i));
			i = i + 1;
		}
		this.CurrentBuilder.Append(")");
	}

	@Override protected void GenerateTypeName(BType Type) {
		if(Type.IsFuncType()) {
			this.GenerateFuncTypeName(Type, "");
		}
		else {
			this.CurrentBuilder.Append(this.GetCTypeName(Type.GetRealType()));
		}
	}

	@Override protected void VisitVarDeclNode(BunLetVarNode Node) {
		this.GenerateTypeName(Node.DeclType());
		this.CurrentBuilder.Append(" ");
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
		this.GenerateCode2(" = ", null, Node.InitValueNode(), this.SemiColon);
		if(Node.HasNextVarNode()) {
			this.VisitVarDeclNode(Node.NextVarNode());
		}
	}

	@Override public void VisitLetNode(BunLetVarNode Node) {
		this.CurrentBuilder.Append("static ");
		this.GenerateTypeName(Node.GetAstType(BunLetVarNode._InitValue));
		this.CurrentBuilder.Append(" ");
		this.CurrentBuilder.Append(Node.GetUniqueName(this));
		this.GenerateCode2(" = ", null, Node.InitValueNode(), this.SemiColon);
	}

	@Override protected void VisitParamNode(BunLetVarNode Node) {
		if(Node.Type.IsFuncType()) {
			this.GenerateFuncTypeName(Node.DeclType(), this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
		}
		else {
			this.GenerateTypeName(Node.DeclType());
			this.CurrentBuilder.Append(" ");
			this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
		}
	}

	@Override public void VisitFunctionNode(BunFunctionNode Node) {
		if(!Node.IsTopLevelDefineFunction()) {
			@Var String FuncName = Node.GetUniqueName(this);
			this.CurrentBuilder = this.InsertNewSourceBuilder();
			this.CurrentBuilder.AppendNewLine("static ");
			this.GenerateTypeName(Node.ReturnType());
			this.CurrentBuilder.Append(" ", FuncName);
			this.VisitFuncParamNode("(", Node, ")");
			this.GenerateCode(null, Node.BlockNode());
			this.CurrentBuilder.AppendLineFeed();
			this.CurrentBuilder = this.CurrentBuilder.Pop();
			this.CurrentBuilder.Append(FuncName);
		}
		else {
			@Var int StartIndex = this.CurrentBuilder.GetPosition();
			this.CurrentBuilder.AppendNewLine("static ");
			this.GenerateTypeName(Node.ReturnType());
			this.CurrentBuilder.Append(" ", Node.GetSignature());
			this.VisitFuncParamNode("(", Node, ")");
			@Var String Prototype = this.CurrentBuilder.CopyString(StartIndex, this.CurrentBuilder.GetPosition());
			this.GenerateCode(null, Node.BlockNode());
			this.CurrentBuilder.AppendLineFeed();

			this.HeaderBuilder.Append(Prototype);
			this.HeaderBuilder.Append(this.SemiColon);
			@Var BFuncType FuncType = Node.GetFuncType();
			if(Node.IsExport) {
				this.GenerateExportFunction(Node);
			}
			if(this.IsMethod(Node.FuncName(), FuncType)) {
				this.HeaderBuilder.AppendNewLine("#define _" + this.NameMethod(FuncType.GetRecvType(), Node.FuncName()));
			}
		}
	}

	private void GenerateExportFunction(BunFunctionNode Node) {
		this.CurrentBuilder.AppendNewLine();
		if(Node.FuncName().equals("main")) {
			this.CurrentBuilder.Append("int");
		}
		else {
			this.GenerateTypeName(Node.ReturnType());
		}
		this.CurrentBuilder.Append(" ", Node.FuncName());
		this.VisitListNode("(", Node, ")");
		this.CurrentBuilder.OpenIndent(" {");
		if(!Node.ReturnType().IsVoidType()) {
			this.CurrentBuilder.AppendNewLine("return ", Node.GetSignature());
		}
		else {
			this.CurrentBuilder.AppendNewLine(Node.GetSignature());
		}
		this.GenerateWrapperCall("(", Node, ")");
		this.CurrentBuilder.Append(this.SemiColon);
		if(Node.FuncName().equals("main")) {
			this.CurrentBuilder.AppendNewLine("return 0;");
		}
		this.CurrentBuilder.CloseIndent("}");
	}

	@Override public void VisitInstanceOfNode(BInstanceOfNode Node) {
		this.CurrentBuilder.Append("LibZen_Is(");
		this.GenerateCode(null, Node.AST[BInstanceOfNode._Left]);
		this.CurrentBuilder.Append(this.Camma);
		this.CurrentBuilder.AppendInt(Node.TargetType().TypeId);
		this.CurrentBuilder.Append(")");
	}

	private void GenerateCField(String CType, String FieldName) {
		this.CurrentBuilder.AppendNewLine(CType, " ");
		this.CurrentBuilder.Append(FieldName, this.SemiColon);
	}

	private void GenerateField(BType DeclType, String FieldName) {
		this.CurrentBuilder.AppendNewLine();
		this.GenerateTypeName(DeclType);
		this.CurrentBuilder.Append(" ", FieldName, this.SemiColon);
	}

	private void GenerateFields(BClassType ClassType, BType ThisType) {
		@Var BType SuperType = ThisType.GetSuperType();
		if(!SuperType.Equals(BClassType._ObjectType)) {
			this.GenerateFields(ClassType, SuperType);
		}
		@Var int i = 0;
		this.GenerateCField("int", "_classId" + ThisType.TypeId);
		this.GenerateCField("int", "_delta" + ThisType.TypeId);
		while (i < ClassType.GetFieldSize()) {
			@Var BClassField ClassField = ClassType.GetFieldAt(i);
			if(ClassField.ClassType == ThisType) {
				this.GenerateField(ClassField.FieldType, ClassField.FieldName);
			}
			i = i + 1;
		}
	}

	@Override public void VisitClassNode(BunClassNode Node) {
		this.CurrentBuilder.AppendNewLine("struct ", this.NameClass(Node.ClassType));
		this.CurrentBuilder.OpenIndent(" {");
		this.GenerateFields(Node.ClassType, Node.ClassType);
		this.GenerateCField("int", "_nextId");
		this.CurrentBuilder.CloseIndent("}");

		this.CurrentBuilder.AppendNewLine("static void _Init", this.NameClass(Node.ClassType), "(");
		this.GenerateTypeName(Node.ClassType);
		this.CurrentBuilder.OpenIndent(" o) {");
		@Var BType SuperType = Node.ClassType.GetSuperType();
		if(!SuperType.Equals(BClassType._ObjectType)) {
			this.CurrentBuilder.AppendNewLine("_Init", this.NameClass(SuperType), "((");
			this.GenerateTypeName(SuperType);
			this.CurrentBuilder.Append(")o);");
		}
		this.CurrentBuilder.AppendNewLine("o->_classId" + Node.ClassType.TypeId, " = " + Node.ClassType.TypeId, this.SemiColon);
		this.CurrentBuilder.AppendNewLine("o->_delta" + Node.ClassType.TypeId, " = sizeof(struct " + this.NameClass(Node.ClassType)+ ") - ");
		if(SuperType.Equals(BClassType._ObjectType)) {
			this.CurrentBuilder.Append("sizeof(int);");
		}
		else {
			this.CurrentBuilder.Append("sizeof(struct " + this.NameClass(SuperType) + ");");
		}
		@Var int i = 0;
		while (i < Node.GetListSize()) {
			@Var BunLetVarNode FieldNode = Node.GetFieldNode(i);
			this.CurrentBuilder.AppendNewLine("o->", FieldNode.GetGivenName(), " = ");
			if(FieldNode.DeclType().IsFuncType()) {
				this.CurrentBuilder.Append("NULL");
			}
			else {
				this.GenerateCode(null, FieldNode.InitValueNode());
			}
			this.CurrentBuilder.Append(this.SemiColon);
			i = i + 1;
		}

		i = 0;
		while (i < Node.ClassType.GetFieldSize()) {
			@Var BClassField ClassField = Node.ClassType.GetFieldAt(i);
			if(ClassField.FieldType.IsFuncType()) {
				this.CurrentBuilder.AppendLineFeed();
				this.CurrentBuilder.Append("#ifdef ", this.NameMethod(Node.ClassType, ClassField.FieldName));
				this.CurrentBuilder.AppendNewLine("o->", ClassField.FieldName, " = ");
				this.CurrentBuilder.Append(BFunc._StringfySignature(ClassField.FieldName, ClassField.FieldType.GetParamSize()-1, Node.ClassType));
				this.CurrentBuilder.AppendLineFeed();
				this.CurrentBuilder.Append("#endif");
			}
			i = i + 1;
		}

		this.CurrentBuilder.AppendNewLine("o->_nextId = 0;");
		this.CurrentBuilder.CloseIndent("}");

		this.CurrentBuilder.AppendNewLine("static ");
		this.GenerateTypeName(Node.ClassType);
		this.CurrentBuilder.Append(" _New", this.NameClass(Node.ClassType));
		this.CurrentBuilder.OpenIndent("(void) {");
		this.CurrentBuilder.AppendNewLine();
		this.GenerateTypeName(Node.ClassType);
		this.CurrentBuilder.Append("o = LibZen_Malloc(sizeof(struct ", this.NameClass(Node.ClassType), "));");
		this.CurrentBuilder.AppendNewLine("_Init", this.NameClass(Node.ClassType), "(o);");
		this.CurrentBuilder.AppendNewLine("return o;");
		this.CurrentBuilder.CloseIndent("}");
	}

}
