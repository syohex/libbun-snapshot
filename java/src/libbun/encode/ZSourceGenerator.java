// ***************************************************************************
// Copyright (c) 2013-2014, Libbun project authors. All rights reserved.
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

import libbun.lang.bun.BunTypeSafer;
import libbun.parser.BGenerator;
import libbun.parser.BLangInfo;
import libbun.parser.BLogger;
import libbun.parser.BNameSpace;
import libbun.parser.BToken;
import libbun.parser.ast.BAsmNode;
import libbun.parser.ast.BBooleanNode;
import libbun.parser.ast.BFloatNode;
import libbun.parser.ast.BGetNameNode;
import libbun.parser.ast.BIntNode;
import libbun.parser.ast.BLetVarNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BNullNode;
import libbun.parser.ast.BSetNameNode;
import libbun.parser.ast.BStringNode;
import libbun.parser.ast.ZAndNode;
import libbun.parser.ast.ZArrayLiteralNode;
import libbun.parser.ast.ZBinaryNode;
import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.ZBreakNode;
import libbun.parser.ast.ZCastNode;
import libbun.parser.ast.ZClassNode;
import libbun.parser.ast.ZComparatorNode;
import libbun.parser.ast.ZDesugarNode;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.ZFuncCallNode;
import libbun.parser.ast.ZFuncNameNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.ZGetIndexNode;
import libbun.parser.ast.ZGetterNode;
import libbun.parser.ast.ZGroupNode;
import libbun.parser.ast.ZIfNode;
import libbun.parser.ast.ZInstanceOfNode;
import libbun.parser.ast.ZListNode;
import libbun.parser.ast.ZLocalDefinedNode;
import libbun.parser.ast.ZMacroNode;
import libbun.parser.ast.ZMapEntryNode;
import libbun.parser.ast.ZMapLiteralNode;
import libbun.parser.ast.ZMethodCallNode;
import libbun.parser.ast.ZNewObjectNode;
import libbun.parser.ast.ZNotNode;
import libbun.parser.ast.ZOrNode;
import libbun.parser.ast.ZReturnNode;
import libbun.parser.ast.ZSetIndexNode;
import libbun.parser.ast.ZSetterNode;
import libbun.parser.ast.ZSugarNode;
import libbun.parser.ast.ZThrowNode;
import libbun.parser.ast.ZTopLevelNode;
import libbun.parser.ast.ZTryNode;
import libbun.parser.ast.ZUnaryNode;
import libbun.parser.ast.ZVarBlockNode;
import libbun.parser.ast.ZWhileNode;
import libbun.type.BClassType;
import libbun.type.BFuncType;
import libbun.type.BType;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.Nullable;
import libbun.util.Var;
import libbun.util.BArray;
import libbun.util.BMap;
import libbun.util.ZenMethod;

public class ZSourceGenerator extends BGenerator {

	@BField public BMap<String> NativeTypeMap = new BMap<String>(null);
	@BField public BMap<String> ReservedNameMap = new BMap<String>(null);

	@BField private final BArray<ZSourceBuilder> BuilderList = new BArray<ZSourceBuilder>(new ZSourceBuilder[4]);
	@BField protected ZSourceBuilder HeaderBuilder;
	@BField protected ZSourceBuilder CurrentBuilder;

	@BField public boolean IsDynamicLanguage = false;
	@BField public String Tab = "   ";
	@BField public String LineFeed = "\n";
	@BField public String LineComment = "//";
	@BField public String BeginComment = "/*";
	@BField public String EndComment = "*/";
	@BField public String SemiColon = ";";
	@BField public String Camma = ", ";

	@BField public String StringLiteralPrefix = "";
	@BField public String IntLiteralSuffix = "";

	@BField public String TrueLiteral = "true";
	@BField public String FalseLiteral = "false";
	@BField public String NullLiteral = "null";

	@BField public String NotOperator = "!";
	@BField public String AndOperator = "&&";
	@BField public String OrOperator = "||";

	@BField public String TopType = "var";
	@BField public String ErrorFunc = "perror";

	@BField public boolean ReadableCode = true;

	public ZSourceGenerator(String Extension, String LangVersion) {
		super(new BLangInfo(LangVersion, Extension));
		this.InitBuilderList();
		this.SetTypeChecker(new BunTypeSafer(this));
	}

	@ZenMethod protected void InitBuilderList() {
		this.CurrentBuilder = null;
		this.BuilderList.clear(0);
		this.HeaderBuilder = this.AppendNewSourceBuilder();
		this.CurrentBuilder = this.AppendNewSourceBuilder();
	}

	@ZenMethod protected void Finish(String FileName) {

	}

	protected final ZSourceBuilder AppendNewSourceBuilder() {
		@Var ZSourceBuilder Builder = new ZSourceBuilder(this, this.CurrentBuilder);
		this.BuilderList.add(Builder);
		return Builder;
	}

	protected final ZSourceBuilder InsertNewSourceBuilder() {
		@Var ZSourceBuilder Builder = new ZSourceBuilder(this, this.CurrentBuilder);
		@Var int i = 0;
		while(i < this.BuilderList.size()) {
			if(this.BuilderList.ArrayValues[i] == this.CurrentBuilder) {
				this.BuilderList.add(i, Builder);
				return Builder;
			}
			i = i + 1;
		}
		this.BuilderList.add(Builder);
		return Builder;
	}

	@Override protected void GenerateImportLibrary(String LibName) {
		this.HeaderBuilder.AppendNewLine("require ", LibName, this.SemiColon);
	}

	protected void SetNativeType(BType Type, String TypeName) {
		@Var String Key = "" + Type.TypeId;
		this.NativeTypeMap.put(Key, TypeName);
	}

	protected String GetNativeTypeName(BType Type) {
		@Var String Key = "" + Type.TypeId;
		@Var String TypeName = this.NativeTypeMap.GetOrNull(Key);
		if (TypeName == null) {
			return Type.ShortName;
		}
		return TypeName;
	}

	public final void SetReservedName(String Keyword, @Nullable String AnotherName) {
		if(AnotherName == null) {
			AnotherName = "_" + Keyword;
		}
		this.ReservedNameMap.put(Keyword, AnotherName);
	}

	//	public String NameLocalVariable(String Name, int Index) {
	//		if(Index == 0) {
	//			@Var String SafeName = this.ReservedNameMap.GetOrNull(Name);
	//			if(SafeName == null) {
	//				SafeName = Name;
	//			}
	//			return SafeName;
	//		}
	//		return Name + "__" + Index;
	//	}

	//	protected void GenerateName(BNode Node) {
	//		if(Node instanceof BGetNameNode) {
	//			@Var BGetNameNode NameNode = (BGetNameNode)Node;
	//			@Var String Name = NameNode.GetName();
	//			if(NameNode.ResolvedNode != null) {
	//				@Var String SafeName = this.ReservedNameMap.GetOrNull(Name);
	//				if(SafeName != null) {
	//					Name = SafeName;
	//				}
	//				@Var int NameIndex = Node.GetNameSpace().GetNameIndex(Name);
	//				if(NameIndex > 0) {
	//					Name = Name + "__" + NameIndex;
	//				}
	//			}
	//			this.CurrentBuilder.Append(Name);
	//			return;
	//		}
	//		this.CurrentBuilder.Append(Node.toString());
	//	}

	public String NameLocalVariable(BNameSpace NameSpace, String Name) {
		@Var String SafeName = this.ReservedNameMap.GetOrNull(Name);
		if(SafeName != null) {
			Name = SafeName;
		}
		@Var int NameIndex = NameSpace.GetNameIndex(Name);
		if(NameIndex > 0) {
			Name = Name + "__" + NameIndex;
		}
		return Name;
	}

	@Override public final void WriteTo(@Nullable String FileName) {
		this.Finish(FileName);
		this.Logger.OutputErrorsToStdErr();
		BLib._WriteTo(this.LangInfo.NameOutputFile(FileName), this.BuilderList);
		this.InitBuilderList();
	}

	@Override public final String GetSourceText() {
		this.Finish(null);
		@Var ZSourceBuilder sb = new ZSourceBuilder(this, null);
		@Var int i = 0;
		while(i < this.BuilderList.size()) {
			@Var ZSourceBuilder Builder = this.BuilderList.ArrayValues[i];
			sb.Append(Builder.toString());
			Builder.Clear();
			sb.AppendLineFeed();
			sb.AppendLineFeed();
			i = i + 1;
		}
		this.InitBuilderList();
		return BLib._SourceBuilderToString(sb);
	}

	@Override public void Perform() {
		@Var int i = 0;
		//this.Logger.OutputErrorsToStdErr();
		BLib._PrintLine("---");
		while(i < this.BuilderList.size()) {
			@Var ZSourceBuilder Builder = this.BuilderList.ArrayValues[i];
			BLib._PrintLine(Builder.toString());
			Builder.Clear();
			i = i + 1;
		}
		this.InitBuilderList();
	}

	protected final void GenerateCode2(String Pre, BType ContextType, BNode Node, String Post) {
		if(Pre != null && Pre.length() > 0) {
			this.CurrentBuilder.Append(Pre);
		}
		this.GenerateCode(ContextType, Node);
		if(Post != null && Post.length() > 0) {
			this.CurrentBuilder.Append(Post);
		}
	}

	protected final void GenerateCode2(String Pre, BType ContextType, BNode Node, String Delim, BType ContextType2, BNode Node2, String Post) {
		if(Pre != null && Pre.length() > 0) {
			this.CurrentBuilder.Append(Pre);
		}
		this.GenerateCode(ContextType, Node);
		if(Delim != null && Delim.length() > 0) {
			this.CurrentBuilder.Append(Delim);
		}
		this.GenerateCode(ContextType2, Node2);
		if(Post != null && Post.length() > 0) {
			this.CurrentBuilder.Append(Post);
		}
	}

	protected final void GenerateCode2(String Pre, BNode Node, String Delim, BNode Node2, String Post) {
		this.GenerateCode2(Pre, null, Node, Delim, null, Node2, Post);
	}

	final protected boolean IsNeededSurroud(BNode Node) {
		if(Node instanceof ZBinaryNode) {
			return true;
		}
		return false;
	}

	protected void GenerateSurroundCode(BNode Node) {
		if(this.IsNeededSurroud(Node)) {
			this.GenerateCode2("(", null, Node, ")");
		}
		else {
			this.GenerateCode(null, Node);
		}
	}

	protected void GenerateStatementEnd() {
		if(this.SemiColon != null && (!this.CurrentBuilder.EndsWith("}") || !this.CurrentBuilder.EndsWith(this.SemiColon))) {
			this.CurrentBuilder.Append(this.SemiColon);
		}
	}

	@Override public void GenerateStatement(BNode Node) {
		this.CurrentBuilder.AppendNewLine();
		if(Node instanceof ZCastNode && Node.Type == BType.VoidType) {
			Node.AST[ZCastNode._Expr].Accept(this);
		}
		else {
			Node.Accept(this);
		}
		this.GenerateStatementEnd();
	}

	protected void VisitStmtList(ZBlockNode Node) {
		@Var int i = 0;
		while (i < Node.GetListSize()) {
			@Var BNode SubNode = Node.GetListAt(i);
			this.GenerateStatement(SubNode);
			i = i + 1;
		}
	}

	@Override public void VisitBlockNode(ZBlockNode Node) {
		this.CurrentBuilder.AppendWhiteSpace();
		this.CurrentBuilder.OpenIndent("{");
		this.VisitStmtList(Node);
		this.CurrentBuilder.CloseIndent("}");
	}

	protected void VisitVarDeclNode(BLetVarNode Node) {
		this.CurrentBuilder.Append("var ", this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
		this.GenerateTypeAnnotation(Node.DeclType());
		this.GenerateCode2(" = ", null, Node.InitValueNode(), this.SemiColon);
		if(Node.HasNextVarNode()) {
			this.CurrentBuilder.AppendNewLine();
			this.VisitVarDeclNode(Node.NextVarNode());
		}
	}

	@Override public void VisitVarBlockNode(ZVarBlockNode Node) {
		this.CurrentBuilder.AppendWhiteSpace();
		this.VisitVarDeclNode(Node.VarDeclNode());
		this.VisitStmtList(Node);
	}





	@Override public void VisitNullNode(BNullNode Node) {
		this.CurrentBuilder.Append(this.NullLiteral);
	}

	@Override public void VisitBooleanNode(BBooleanNode Node) {
		if (Node.BooleanValue) {
			this.CurrentBuilder.Append(this.TrueLiteral);
		} else {
			this.CurrentBuilder.Append(this.FalseLiteral);
		}
	}

	@Override public void VisitIntNode(BIntNode Node) {
		this.CurrentBuilder.Append(String.valueOf(Node.IntValue), this.IntLiteralSuffix);
	}

	@Override public void VisitFloatNode(BFloatNode Node) {
		this.CurrentBuilder.Append(String.valueOf(Node.FloatValue));
	}

	@Override public void VisitStringNode(BStringNode Node) {
		this.CurrentBuilder.Append(this.StringLiteralPrefix, BLib._QuoteString(Node.StringValue));
	}

	@Override public void VisitArrayLiteralNode(ZArrayLiteralNode Node) {
		this.VisitListNode("[", Node, "]");
	}

	@Override public void VisitMapLiteralNode(ZMapLiteralNode Node) {
		this.CurrentBuilder.Append("{");
		@Var int i = 0;
		while(i < Node.GetListSize()) {
			@Var ZMapEntryNode Entry = Node.GetMapEntryNode(i);
			this.GenerateCode2("", Entry.KeyNode(), ": ", Entry.ValueNode(), ",");
			i = i + 1;
		}
		this.CurrentBuilder.Append("} ");  // space is needed to distinguish block
	}

	@Override public void VisitNewObjectNode(ZNewObjectNode Node) {
		this.CurrentBuilder.Append("new ");
		this.GenerateTypeName(Node.Type);
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitGroupNode(ZGroupNode Node) {
		this.GenerateCode2("(", null, Node.ExprNode(), ")");
	}

	@Override public void VisitGetIndexNode(ZGetIndexNode Node) {
		this.GenerateCode(null, Node.RecvNode());
		this.GenerateCode2("[", null, Node.IndexNode(), "]");
	}

	@Override public void VisitSetIndexNode(ZSetIndexNode Node) {
		this.GenerateCode(null, Node.RecvNode());
		this.GenerateCode2("[", null, Node.IndexNode(), "] = ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitGetNameNode(BGetNameNode Node) {
		@Var BNode ResolvedNode = Node.ResolvedNode;
		if(ResolvedNode == null && !this.LangInfo.AllowUndefinedSymbol) {
			BLogger._LogError(Node.SourceToken, "undefined symbol: " + Node.GivenName);
		}
		this.CurrentBuilder.Append(Node.GetUniqueName(this));
	}

	@Override public void VisitSetNameNode(BSetNameNode Node) {
		this.VisitGetNameNode(Node.NameNode());
		this.CurrentBuilder.Append(" = ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitGetterNode(ZGetterNode Node) {
		this.GenerateSurroundCode(Node.RecvNode());
		this.CurrentBuilder.Append(".", Node.GetName());
	}

	@Override public void VisitSetterNode(ZSetterNode Node) {
		this.GenerateSurroundCode(Node.RecvNode());
		this.CurrentBuilder.Append(".", Node.GetName(), " = ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitMethodCallNode(ZMethodCallNode Node) {
		this.GenerateSurroundCode(Node.RecvNode());
		this.CurrentBuilder.Append(".", Node.MethodName());
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitMacroNode(ZMacroNode Node) {
		@Var String Macro = Node.GetMacroText();
		@Var BFuncType FuncType = Node.GetFuncType();
		@Var int fromIndex = 0;
		@Var int BeginNum = Macro.indexOf("$[", fromIndex);
		while(BeginNum != -1) {
			@Var int EndNum = Macro.indexOf("]", BeginNum + 2);
			if(EndNum == -1) {
				break;
			}
			this.CurrentBuilder.Append(Macro.substring(fromIndex, BeginNum));
			@Var int Index = (int)BLib._ParseInt(Macro.substring(BeginNum+2, EndNum));
			if(Node.AST[Index] != null) {
				this.GenerateCode(FuncType.GetFuncParamType(Index), Node.AST[Index]);
			}
			fromIndex = EndNum + 1;
			BeginNum = Macro.indexOf("$[", fromIndex);
		}
		this.CurrentBuilder.Append(Macro.substring(fromIndex));
		if(Node.MacroFunc.RequiredLibrary != null) {
			this.ImportLibrary(Node.MacroFunc.RequiredLibrary);
		}
	}

	protected final void GenerateFuncName(ZFuncNameNode Node) {
		if(this.LangInfo.AllowFunctionOverloading) {
			this.CurrentBuilder.Append(Node.FuncName);
		}
		else {
			this.CurrentBuilder.Append(Node.GetSignature());
		}
	}

	@Override public void VisitFuncCallNode(ZFuncCallNode Node) {
		@Var ZFuncNameNode FuncNameNode = Node.FuncNameNode();
		if(FuncNameNode != null) {
			this.GenerateFuncName(FuncNameNode);
		}
		else {
			this.GenerateCode(null, Node.FunctorNode());
		}
		this.VisitListNode("(", Node, ")");
	}

	@ZenMethod protected String GetUnaryOperator(BType Type, BToken Token) {
		if(Token.EqualsText('-')) {
			return "-";
		}
		if(Token.EqualsText('+')) {
			return "+";
		}
		return Token.GetText();
	}

	@Override public void VisitUnaryNode(ZUnaryNode Node) {
		this.CurrentBuilder.Append(this.GetUnaryOperator(Node.Type, Node.SourceToken));
		this.GenerateCode(null, Node.RecvNode());
	}

	@Override public void VisitNotNode(ZNotNode Node) {
		this.CurrentBuilder.Append(this.NotOperator);
		this.GenerateSurroundCode(Node.RecvNode());
	}

	@Override public void VisitCastNode(ZCastNode Node) {
		if(Node.Type.IsVoidType()) {
			this.GenerateCode(null, Node.ExprNode());
		}
		else {
			this.CurrentBuilder.Append("(");
			this.GenerateTypeName(Node.Type);
			this.CurrentBuilder.Append(")");
			this.GenerateSurroundCode(Node.ExprNode());
		}
	}

	@Override public void VisitInstanceOfNode(ZInstanceOfNode Node) {
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.Append(" instanceof ");
		this.GenerateTypeName(Node.TargetType());
	}

	@ZenMethod protected String GetBinaryOperator(BType Type, BToken Token) {
		if(Token.EqualsText('+')) {
			return "+";
		}
		if(Token.EqualsText('-')) {
			return "-";
		}
		return Token.GetText();
	}

	@Override public void VisitBinaryNode(ZBinaryNode Node) {
		if (Node.ParentNode instanceof ZBinaryNode) {
			this.CurrentBuilder.Append("(");
		}
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.AppendToken(this.GetBinaryOperator(Node.Type, Node.SourceToken));
		this.GenerateCode(null, Node.RightNode());
		if (Node.ParentNode instanceof ZBinaryNode) {
			this.CurrentBuilder.Append(")");
		}
	}

	@Override public void VisitComparatorNode(ZComparatorNode Node) {
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.AppendToken(this.GetBinaryOperator(Node.Type, Node.SourceToken));
		this.GenerateCode(null, Node.RightNode());
	}

	@Override public void VisitAndNode(ZAndNode Node) {
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.AppendToken(this.AndOperator);
		this.GenerateCode(null, Node.RightNode());
	}

	@Override public void VisitOrNode(ZOrNode Node) {
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.AppendToken(this.OrOperator);
		this.GenerateCode(null, Node.RightNode());
	}

	@Override public void VisitIfNode(ZIfNode Node) {
		this.GenerateCode2("if (", null, Node.CondNode(), ")");
		this.GenerateCode(null, Node.ThenNode());
		if (Node.HasElseNode()) {
			this.CurrentBuilder.AppendNewLine();
			this.CurrentBuilder.Append("else ");
			this.GenerateCode(null, Node.ElseNode());
		}
	}

	@Override public void VisitReturnNode(ZReturnNode Node) {
		this.CurrentBuilder.Append("return");
		if (Node.HasReturnExpr()) {
			this.CurrentBuilder.Append(" ");
			this.GenerateCode(null, Node.ExprNode());
		}
	}

	@Override public void VisitWhileNode(ZWhileNode Node) {
		this.GenerateCode2("while (", null, Node.CondNode(),")");
		this.GenerateCode(null, Node.BlockNode());
	}

	@Override public void VisitBreakNode(ZBreakNode Node) {
		this.CurrentBuilder.Append("break");
	}

	@Override public void VisitThrowNode(ZThrowNode Node) {
		this.CurrentBuilder.Append("throw ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitTryNode(ZTryNode Node) {
		this.CurrentBuilder.Append("try");
		this.GenerateCode(null, Node.TryBlockNode());
		if(Node.HasCatchBlockNode()) {
			this.CurrentBuilder.AppendNewLine("catch (", Node.ExceptionName());
			this.CurrentBuilder.Append(") ");
			this.GenerateCode(null, Node.CatchBlockNode());
		}
		if (Node.HasFinallyBlockNode()) {
			this.CurrentBuilder.AppendNewLine("finally ");
			this.GenerateCode(null, Node.FinallyBlockNode());
		}
	}

	protected void GenerateTypeAnnotation(BType Type) {
		if(!Type.IsVarType()) {
			this.CurrentBuilder.Append(": ");
			this.GenerateTypeName(Type);
		}
	}

	@Override public void VisitLetNode(BLetVarNode Node) {
		if(Node.IsParamNode()) {
			this.VisitParamNode(Node);
		}
		else {
			this.CurrentBuilder.AppendNewLine("let ", Node.GetGivenName());
			this.GenerateTypeAnnotation(Node.DeclType());
			this.CurrentBuilder.Append(" = ");
			this.GenerateCode(null, Node.InitValueNode());
		}
	}

	protected void VisitParamNode(BLetVarNode Node) {
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
		this.GenerateTypeAnnotation(Node.Type);
	}

	protected void VisitFuncParamNode(String OpenToken, ZFunctionNode VargNode, String CloseToken) {
		this.CurrentBuilder.Append(OpenToken);
		@Var int i = 0;
		while(i < VargNode.GetListSize()) {
			@Var BLetVarNode ParamNode = VargNode.GetParamNode(i);
			if (i > 0) {
				this.CurrentBuilder.Append(this.Camma);
			}
			this.VisitParamNode(ParamNode);
			i = i + 1;
		}
		this.CurrentBuilder.Append(CloseToken);
	}

	@Override public void VisitFunctionNode(ZFunctionNode Node) {
		if(Node.IsExport) {
			this.CurrentBuilder.Append("export ");
		}
		this.CurrentBuilder.Append("function ");
		if(Node.FuncName() != null) {
			this.CurrentBuilder.Append(Node.FuncName());
		}
		this.VisitFuncParamNode("(", Node, ")");
		this.GenerateTypeAnnotation(Node.ReturnType());
		this.GenerateCode(null, Node.BlockNode());
	}

	@Override public void VisitClassNode(ZClassNode Node) {
		this.CurrentBuilder.AppendNewLine("class ", Node.ClassName());
		if(Node.SuperType() != null) {
			this.CurrentBuilder.Append(" extends ");
			this.GenerateTypeName(Node.SuperType());
		}
		this.CurrentBuilder.OpenIndent(" {");
		@Var int i = 0;
		while (i < Node.GetListSize()) {
			@Var BLetVarNode FieldNode = Node.GetFieldNode(i);
			this.CurrentBuilder.AppendNewLine("var ", FieldNode.GetGivenName());
			this.GenerateTypeAnnotation(FieldNode.DeclType());
			this.CurrentBuilder.AppendToken("=");
			this.GenerateCode(null, FieldNode.InitValueNode());
			this.CurrentBuilder.Append(this.SemiColon);
			i = i + 1;
		}
		this.CurrentBuilder.CloseIndent("}");
	}

	@Override public void VisitErrorNode(ZErrorNode Node) {
		@Var String Message = BLogger._LogError(Node.SourceToken, Node.ErrorMessage);
		this.CurrentBuilder.Append(this.ErrorFunc, "(");
		this.CurrentBuilder.Append(BLib._QuoteString(Message));
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitAsmNode(BAsmNode Node) {
		this.ImportLibrary(Node.RequiredLibrary);
		this.CurrentBuilder.AppendCode(Node.GetMacroText());
	}

	@Override public void VisitLocalDefinedNode(ZLocalDefinedNode Node) {
		this.VisitUndefinedNode(Node);
	}

	@Override public void VisitTopLevelNode(ZTopLevelNode Node) {
		this.VisitUndefinedNode(Node);
	}

	@Override public void VisitSugarNode(ZSugarNode Node) {
		@Var ZDesugarNode DesugarNode = Node.DeSugar(this, this.TypeChecker);
		this.GenerateCode(null, DesugarNode.AST[0]);
		@Var int i = 1;
		while(i < DesugarNode.GetAstSize()) {
			this.CurrentBuilder.Append(this.SemiColon);
			this.CurrentBuilder.AppendNewLine();
			this.GenerateCode(null, DesugarNode.AST[i]);
			i = i + 1;
		}
	}

	// Utils
	protected void GenerateTypeName(BType Type) {
		this.CurrentBuilder.Append(this.GetNativeTypeName(Type.GetRealType()));
	}

	protected void VisitListNode(String OpenToken, ZListNode VargNode, String DelimToken, String CloseToken) {
		this.CurrentBuilder.Append(OpenToken);
		@Var int i = 0;
		while(i < VargNode.GetListSize()) {
			@Var BNode ParamNode = VargNode.GetListAt(i);
			if (i > 0) {
				this.CurrentBuilder.Append(DelimToken);
			}
			this.GenerateCode(null, ParamNode);
			i = i + 1;
		}
		this.CurrentBuilder.Append(CloseToken);
	}

	protected void VisitListNode(String OpenToken, ZListNode VargNode, String CloseToken) {
		this.VisitListNode(OpenToken, VargNode, this.Camma, CloseToken);
	}

	protected void GenerateWrapperCall(String OpenToken, ZFunctionNode FuncNode, String CloseToken) {
		this.CurrentBuilder.Append(OpenToken);
		@Var int i = 0;
		while(i < FuncNode.GetListSize()) {
			@Var BLetVarNode ParamNode = FuncNode.GetParamNode(i);
			if (i > 0) {
				this.CurrentBuilder.Append(this.Camma);
			}
			this.CurrentBuilder.Append(this.NameLocalVariable(ParamNode.GetNameSpace(), ParamNode.GetGivenName()));
			i = i + 1;
		}
		this.CurrentBuilder.Append(CloseToken);
	}

	protected final String NameMethod(BType ClassType, String MethodName) {
		return "_" + this.NameClass(ClassType) + "_" + MethodName;
	}

	protected final boolean IsMethod(String FuncName, BFuncType FuncType) {
		@Var BType RecvType = FuncType.GetRecvType();
		if(RecvType instanceof BClassType && FuncName != null) {
			@Var BClassType ClassType = (BClassType)RecvType;
			@Var BType FieldType = ClassType.GetFieldType(FuncName, null);
			if(FieldType == null || !FieldType.IsFuncType()) {
				FuncName = BLib._AnotherName(FuncName);
				FieldType = ClassType.GetFieldType(FuncName, null);
				if(FieldType == null || !FieldType.IsFuncType()) {
					return false;
				}
			}
			if(FieldType instanceof BFuncType) {
				if(((BFuncType)FieldType).AcceptAsFieldFunc(FuncType)) {
					return true;
				}
			}
		}
		return false;
	}
}
