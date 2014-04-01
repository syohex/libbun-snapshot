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

import libbun.parser.ZLogger;
import libbun.parser.ast.ZArrayLiteralNode;
import libbun.parser.ast.ZClassNode;
import libbun.parser.ast.ZFuncCallNode;
import libbun.parser.ast.ZFuncNameNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.ZGetIndexNode;
import libbun.parser.ast.ZGetNameNode;
import libbun.parser.ast.ZGetterNode;
import libbun.parser.ast.ZInstanceOfNode;
import libbun.parser.ast.ZLetVarNode;
import libbun.parser.ast.ZLocalDefinedNode;
import libbun.parser.ast.ZMapLiteralNode;
import libbun.parser.ast.ZMethodCallNode;
import libbun.parser.ast.ZNewObjectNode;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZSetIndexNode;
import libbun.parser.ast.ZSetNameNode;
import libbun.parser.ast.ZSetterNode;
import libbun.parser.ast.ZThrowNode;
import libbun.parser.ast.ZTryNode;
import libbun.parser.ssa.NodeLib;
import libbun.parser.ssa.PHINode;
import libbun.parser.ssa.SSAConverter;
import libbun.type.ZClassField;
import libbun.type.ZClassType;
import libbun.type.ZFunc;
import libbun.type.ZFuncType;
import libbun.type.ZType;
import libbun.util.Var;
import libbun.util.ZArray;

public class SSACGenerator extends ZSourceGenerator {

	public SSACGenerator() {
		super("c", "C99");
		this.TrueLiteral  = "1/*true*/";
		this.FalseLiteral = "0/*false*/";
		this.NullLiteral  = "NULL";

		this.TopType = "ZObject *";
		this.SetNativeType(ZType.BooleanType, "int");
		//		this.SetNativeType(ZType.IntType, "long long int");
		this.SetNativeType(ZType.IntType, "long");
		this.SetNativeType(ZType.FloatType, "double");
		this.SetNativeType(ZType.StringType, "const char *");

		this.HeaderBuilder.AppendNewLine("/* end of header */", this.LineFeed);
	}

	@Override protected void GenerateImportLibrary(String LibName) {
		this.HeaderBuilder.AppendNewLine("#include<", LibName, ">");
	}

	@Override protected void GenerateCode(ZType ContextType, ZNode Node) {
		if(Node.IsUntyped() && !Node.IsErrorNode() && !(Node instanceof ZFuncNameNode)) {
			this.CurrentBuilder.Append("/*untyped*/" + this.NullLiteral);
			ZLogger._LogError(Node.SourceToken, "untyped error: " + Node);
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

	@Override public void VisitGetNameNode(ZGetNameNode Node) {
		if(Node.ResolvedNode == null && !this.LangInfo.AllowUndefinedSymbol) {
			ZLogger._LogError(Node.SourceToken, "undefined symbol: " + Node.GetName());
		}
		if(Node.IsGlobalName()) {
			this.CurrentBuilder.Append(Node.GetName() + Node.VarIndex);
		}
		else {
			this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetName()) + Node.VarIndex);
		}
	}

	@Override public void VisitSetNameNode(ZSetNameNode Node) {
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetName())  + Node.VarIndex, " = ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitArrayLiteralNode(ZArrayLiteralNode Node) {
		@Var ZType ParamType = Node.Type.GetParamType(0);
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

	@Override public void VisitMapLiteralNode(ZMapLiteralNode Node) {
		@Var ZType ParamType = Node.Type.GetParamType(0);
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

	@Override public void VisitNewObjectNode(ZNewObjectNode Node) {
		this.CurrentBuilder.Append("_New"+this.NameClass(Node.Type));
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitGetIndexNode(ZGetIndexNode Node) {
		this.CurrentBuilder.Append(this.NameType(Node.GetAstType(ZGetIndexNode._Recv)) + "GetIndex");
		this.CurrentBuilder.Append("(");
		this.GenerateCode(null, Node.IndexNode());
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitSetIndexNode(ZSetIndexNode Node) {
		this.CurrentBuilder.Append(this.NameType(Node.GetAstType(ZGetIndexNode._Recv)) + "SetIndex");
		this.CurrentBuilder.Append("(");
		this.GenerateCode(null, Node.IndexNode());
		this.CurrentBuilder.Append(this.Camma);
		this.GenerateCode(null, Node.ExprNode());
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitGetterNode(ZGetterNode Node) {
		this.GenerateSurroundCode(Node.RecvNode());
		this.CurrentBuilder.Append("->");
		this.CurrentBuilder.Append(Node.GetName());
	}

	@Override public void VisitSetterNode(ZSetterNode Node) {
		this.GenerateSurroundCode(Node.RecvNode());
		this.CurrentBuilder.Append("->");
		this.CurrentBuilder.Append(Node.GetName());
		this.CurrentBuilder.AppendToken("=");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitMethodCallNode(ZMethodCallNode Node) {
		//		this.GenerateSurroundCode(Node.RecvNode());
		//		this.CurrentBuilder.Append(".");
		//		this.CurrentBuilder.Append(Node.MethodName());
		//		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitFuncCallNode(ZFuncCallNode Node) {
		this.GenerateCode(null, Node.FunctorNode());
		this.VisitListNode("(", Node, ")");
	}

	//	@Override public void VisitCastNode(ZCastNode Node) {
	//		this.CurrentBuilder.Append("(");
	//		this.VisitType(Node.Type);
	//		this.CurrentBuilder.Append(")");
	//		this.GenerateSurroundCode(Node.ExprNode());
	//	}

	@Override public void VisitThrowNode(ZThrowNode Node) {
		this.GenerateCode(null, Node.ExprNode());
		this.CurrentBuilder.Append("longjump(1)"); // FIXME
		this.CurrentBuilder.AppendWhiteSpace();
	}

	@Override public void VisitTryNode(ZTryNode Node) {
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

	private String ParamTypeName(ZType Type) {
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

	private String GetCTypeName(ZType Type) {
		@Var String TypeName = null;
		if(Type.IsArrayType() || Type.IsMapType()) {
			TypeName = this.ParamTypeName(Type) + " *";
		}
		if(Type instanceof ZClassType) {
			TypeName = "struct " + this.NameClass(Type) + " *";
		}
		if(TypeName == null) {
			TypeName = this.GetNativeTypeName(Type);
		}
		return TypeName;
	}

	protected void GenerateFuncTypeName(ZType Type, String FuncName) {
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

	@Override protected void GenerateTypeName(ZType Type) {
		if(Type.IsFuncType()) {
			this.GenerateFuncTypeName(Type, "");
		}
		else {
			this.CurrentBuilder.Append(this.GetCTypeName(Type.GetRealType()));
		}
	}

	@Override protected void VisitVarDeclNode(ZLetVarNode Node) {
		this.GenerateTypeName(Node.DeclType());
		this.CurrentBuilder.Append(" ");
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetName() + "0"));
		this.GenerateCode2(" = ", null, Node.InitValueNode(), this.SemiColon);
		if(Node.HasNextVarNode()) {
			this.VisitVarDeclNode(Node.NextVarNode());
		}
	}

	@Override public void VisitLetNode(ZLetVarNode Node) {
		this.CurrentBuilder.Append("static ");
		this.GenerateTypeName(Node.GetAstType(ZLetVarNode._InitValue));
		this.CurrentBuilder.Append(" ");
		this.CurrentBuilder.Append(Node.GlobalName);
		this.GenerateCode2(" = ", null, Node.InitValueNode(), this.SemiColon);
	}

	@Override protected void VisitParamNode(ZLetVarNode Node) {
		if(Node.Type.IsFuncType()) {
			this.GenerateFuncTypeName(Node.DeclType(), this.NameLocalVariable(Node.GetNameSpace(), Node.GetName()));
		}
		else {
			this.GenerateTypeName(Node.DeclType());
			this.CurrentBuilder.Append(" ");
			this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetName()));
		}
	}

	@Override public void VisitFunctionNode(ZFunctionNode Node) {
		Node.Accept(new SSAConverter());
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
			@Var ZFuncType FuncType = Node.GetFuncType();
			if(Node.IsExport) {
				this.GenerateExportFunction(Node);
			}
			if(this.IsMethod(Node.FuncName(), FuncType)) {
				this.HeaderBuilder.AppendNewLine("#define _" + this.NameMethod(FuncType.GetRecvType(), Node.FuncName()));
			}
		}
	}

	private void GenerateExportFunction(ZFunctionNode Node) {
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

	@Override public void VisitInstanceOfNode(ZInstanceOfNode Node) {
		this.CurrentBuilder.Append("LibZen_Is(");
		this.GenerateCode(null, Node.AST[ZInstanceOfNode._Left]);
		this.CurrentBuilder.Append(this.Camma);
		this.CurrentBuilder.AppendInt(Node.TargetType().TypeId);
		this.CurrentBuilder.Append(")");
	}

	private void GenerateCField(String CType, String FieldName) {
		this.CurrentBuilder.AppendNewLine(CType, " ");
		this.CurrentBuilder.Append(FieldName, this.SemiColon);
	}

	private void GenerateField(ZType DeclType, String FieldName) {
		this.CurrentBuilder.AppendNewLine();
		this.GenerateTypeName(DeclType);
		this.CurrentBuilder.Append(" ", FieldName, this.SemiColon);
	}

	private void GenerateFields(ZClassType ClassType, ZType ThisType) {
		@Var ZType SuperType = ThisType.GetSuperType();
		if(!SuperType.Equals(ZClassType._ObjectType)) {
			this.GenerateFields(ClassType, SuperType);
		}
		@Var int i = 0;
		this.GenerateCField("int", "_classId" + ThisType.TypeId);
		this.GenerateCField("int", "_delta" + ThisType.TypeId);
		while (i < ClassType.GetFieldSize()) {
			@Var ZClassField ClassField = ClassType.GetFieldAt(i);
			if(ClassField.ClassType == ThisType) {
				this.GenerateField(ClassField.FieldType, ClassField.FieldName);
			}
			i = i + 1;
		}
	}

	@Override public void VisitClassNode(ZClassNode Node) {
		this.CurrentBuilder.AppendNewLine("struct ", this.NameClass(Node.ClassType));
		this.CurrentBuilder.OpenIndent(" {");
		this.GenerateFields(Node.ClassType, Node.ClassType);
		this.GenerateCField("int", "_nextId");
		this.CurrentBuilder.CloseIndent("}");

		this.CurrentBuilder.AppendNewLine("static void _Init", this.NameClass(Node.ClassType), "(");
		this.GenerateTypeName(Node.ClassType);
		this.CurrentBuilder.OpenIndent(" o) {");
		@Var ZType SuperType = Node.ClassType.GetSuperType();
		if(!SuperType.Equals(ZClassType._ObjectType)) {
			this.CurrentBuilder.AppendNewLine("_Init", this.NameClass(SuperType), "((");
			this.GenerateTypeName(SuperType);
			this.CurrentBuilder.Append(")o);");
		}
		this.CurrentBuilder.AppendNewLine("o->_classId" + Node.ClassType.TypeId, " = " + Node.ClassType.TypeId, this.SemiColon);
		this.CurrentBuilder.AppendNewLine("o->_delta" + Node.ClassType.TypeId, " = sizeof(struct " + this.NameClass(Node.ClassType)+ ") - ");
		if(SuperType.Equals(ZClassType._ObjectType)) {
			this.CurrentBuilder.Append("sizeof(int);");
		}
		else {
			this.CurrentBuilder.Append("sizeof(struct " + this.NameClass(SuperType) + ");");
		}
		@Var int i = 0;
		while (i < Node.GetListSize()) {
			@Var ZLetVarNode FieldNode = Node.GetFieldNode(i);
			this.CurrentBuilder.AppendNewLine("o->", FieldNode.GetName(), " = ");
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
			@Var ZClassField ClassField = Node.ClassType.GetFieldAt(i);
			if(ClassField.FieldType.IsFuncType()) {
				this.CurrentBuilder.AppendLineFeed();
				this.CurrentBuilder.Append("#ifdef ", this.NameMethod(Node.ClassType, ClassField.FieldName));
				this.CurrentBuilder.AppendNewLine("o->", ClassField.FieldName, " = ");
				this.CurrentBuilder.Append(ZFunc._StringfySignature(ClassField.FieldName, ClassField.FieldType.GetParamSize()-1, Node.ClassType));
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

	@Override public void VisitLocalDefinedNode(ZLocalDefinedNode Node) {
		if(!(Node instanceof PHINode)) {
			this.VisitUndefinedNode(Node);
			return;
		}
		PHINode phi = (PHINode) Node;
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), phi.GetName())  + phi.GetVarIndex(), " = ");
		this.CurrentBuilder.Append("phi(");
		@Var int i = 0;
		while(i < phi.Args.size()) {
			ZNode Arg = ZArray.GetIndex(phi.Args, i);
			// Arg is instanceof ZLetVarNode or ZVarBlockNode or ZSetNameNode or PHINode
			if(i != 0) {
				this.CurrentBuilder.Append(", ");
			}
			this.CurrentBuilder.Append(phi.VariableName);
			this.CurrentBuilder.Append("" + NodeLib.GetVarIndex(Arg));
			i = i + 1;
		}
		this.CurrentBuilder.Append(")");
	}

}
