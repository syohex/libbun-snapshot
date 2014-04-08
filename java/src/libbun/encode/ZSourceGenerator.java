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

import libbun.ast.BunBlockNode;
import libbun.ast.DesugarNode;
import libbun.ast.GroupNode;
import libbun.ast.AbstractListNode;
import libbun.ast.BNode;
import libbun.ast.SyntaxSugarNode;
import libbun.ast.LocalDefinedNode;
import libbun.ast.binary.BunAndNode;
import libbun.ast.binary.BinaryOperatorNode;
import libbun.ast.binary.ComparatorNode;
import libbun.ast.binary.BInstanceOfNode;
import libbun.ast.binary.BunOrNode;
import libbun.ast.binary.BunAddNode;
import libbun.ast.binary.BunBitwiseAndNode;
import libbun.ast.binary.BunBitwiseOrNode;
import libbun.ast.binary.BunBitwiseXorNode;
import libbun.ast.binary.BunDivNode;
import libbun.ast.binary.BunEqualsNode;
import libbun.ast.binary.BunGreaterThanEqualsNode;
import libbun.ast.binary.BunGreaterThanNode;
import libbun.ast.binary.BunLeftShiftNode;
import libbun.ast.binary.BunLessThanEqualsNode;
import libbun.ast.binary.BunLessThanNode;
import libbun.ast.binary.BunModNode;
import libbun.ast.binary.BunMulNode;
import libbun.ast.binary.BunNotEqualsNode;
import libbun.ast.binary.BunRightShiftNode;
import libbun.ast.binary.BunSubNode;
import libbun.ast.decl.BunClassNode;
import libbun.ast.decl.BunFunctionNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.ast.decl.TopLevelNode;
import libbun.ast.decl.BunVarBlockNode;
import libbun.ast.error.ErrorNode;
import libbun.ast.expression.FuncCallNode;
import libbun.ast.expression.BunFuncNameNode;
import libbun.ast.expression.GetIndexNode;
import libbun.ast.expression.GetNameNode;
import libbun.ast.expression.GetFieldNode;
import libbun.ast.expression.BunMacroNode;
import libbun.ast.expression.MethodCallNode;
import libbun.ast.expression.NewObjectNode;
import libbun.ast.expression.SetIndexNode;
import libbun.ast.expression.SetNameNode;
import libbun.ast.expression.SetFieldNode;
import libbun.ast.literal.BunArrayLiteralNode;
import libbun.ast.literal.BunAsmNode;
import libbun.ast.literal.BunBooleanNode;
import libbun.ast.literal.BunFloatNode;
import libbun.ast.literal.BunIntNode;
import libbun.ast.literal.BunNullNode;
import libbun.ast.literal.BunStringNode;
import libbun.ast.literal.LiteralNode;
import libbun.ast.literal.BunMapEntryNode;
import libbun.ast.literal.BunMapLiteralNode;
import libbun.ast.statement.BunBreakNode;
import libbun.ast.statement.BunIfNode;
import libbun.ast.statement.BunReturnNode;
import libbun.ast.statement.BunThrowNode;
import libbun.ast.statement.BunTryNode;
import libbun.ast.statement.BunWhileNode;
import libbun.ast.unary.BunCastNode;
import libbun.ast.unary.BunNotNode;
import libbun.ast.unary.UnaryOperatorNode;
import libbun.ast.unary.BunComplementNode;
import libbun.ast.unary.BunMinusNode;
import libbun.ast.unary.BunPlusNode;
import libbun.lang.bun.BunTypeSafer;
import libbun.parser.BGenerator;
import libbun.parser.BLangInfo;
import libbun.parser.BLogger;
import libbun.parser.BNameSpace;
import libbun.type.BClassType;
import libbun.type.BFuncType;
import libbun.type.BType;
import libbun.util.BArray;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.BMap;
import libbun.util.Nullable;
import libbun.util.Var;
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
		if(Node instanceof BinaryOperatorNode) {
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
		if(Node instanceof BunCastNode && Node.Type == BType.VoidType) {
			Node.AST[BunCastNode._Expr].Accept(this);
		}
		else {
			Node.Accept(this);
		}
		this.GenerateStatementEnd();
	}

	protected void VisitStmtList(BunBlockNode Node) {
		@Var int i = 0;
		while (i < Node.GetListSize()) {
			@Var BNode SubNode = Node.GetListAt(i);
			this.GenerateStatement(SubNode);
			i = i + 1;
		}
	}

	@Override public void VisitBlockNode(BunBlockNode Node) {
		this.CurrentBuilder.AppendWhiteSpace();
		this.CurrentBuilder.OpenIndent("{");
		this.VisitStmtList(Node);
		this.CurrentBuilder.CloseIndent("}");
	}

	protected void VisitVarDeclNode(BunLetVarNode Node) {
		this.CurrentBuilder.Append("var ", this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
		this.GenerateTypeAnnotation(Node.DeclType());
		this.GenerateCode2(" = ", null, Node.InitValueNode(), this.SemiColon);
		if(Node.HasNextVarNode()) {
			this.CurrentBuilder.AppendNewLine();
			this.VisitVarDeclNode(Node.NextVarNode());
		}
	}

	@Override public void VisitVarBlockNode(BunVarBlockNode Node) {
		this.CurrentBuilder.AppendWhiteSpace();
		this.VisitVarDeclNode(Node.VarDeclNode());
		this.VisitStmtList(Node);
	}





	@Override public void VisitNullNode(BunNullNode Node) {
		this.CurrentBuilder.Append(this.NullLiteral);
	}

	@Override public void VisitBooleanNode(BunBooleanNode Node) {
		if (Node.BooleanValue) {
			this.CurrentBuilder.Append(this.TrueLiteral);
		} else {
			this.CurrentBuilder.Append(this.FalseLiteral);
		}
	}

	@Override public void VisitIntNode(BunIntNode Node) {
		this.CurrentBuilder.Append(String.valueOf(Node.IntValue), this.IntLiteralSuffix);
	}

	@Override public void VisitFloatNode(BunFloatNode Node) {
		this.CurrentBuilder.Append(String.valueOf(Node.FloatValue));
	}

	@Override public void VisitStringNode(BunStringNode Node) {
		this.CurrentBuilder.Append(this.StringLiteralPrefix, BLib._QuoteString(Node.StringValue));
	}

	@Override public void VisitArrayLiteralNode(BunArrayLiteralNode Node) {
		this.VisitListNode("[", Node, "]");
	}

	@Override public void VisitMapLiteralNode(BunMapLiteralNode Node) {
		this.CurrentBuilder.Append("{");
		@Var int i = 0;
		while(i < Node.GetListSize()) {
			@Var BunMapEntryNode Entry = Node.GetMapEntryNode(i);
			this.GenerateCode2("", Entry.KeyNode(), ": ", Entry.ValueNode(), ",");
			i = i + 1;
		}
		this.CurrentBuilder.Append("} ");  // space is needed to distinguish block
	}

	@Override public void VisitNewObjectNode(NewObjectNode Node) {
		this.CurrentBuilder.Append("new ");
		this.GenerateTypeName(Node.Type);
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitGroupNode(GroupNode Node) {
		this.GenerateCode2("(", null, Node.ExprNode(), ")");
	}

	@Override public void VisitGetIndexNode(GetIndexNode Node) {
		this.GenerateCode(null, Node.RecvNode());
		this.GenerateCode2("[", null, Node.IndexNode(), "]");
	}

	@Override public void VisitSetIndexNode(SetIndexNode Node) {
		this.GenerateCode(null, Node.RecvNode());
		this.GenerateCode2("[", null, Node.IndexNode(), "] = ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitGetNameNode(GetNameNode Node) {
		@Var BNode ResolvedNode = Node.ResolvedNode;
		if(ResolvedNode == null && !this.LangInfo.AllowUndefinedSymbol) {
			BLogger._LogError(Node.SourceToken, "undefined symbol: " + Node.GivenName);
		}
		this.CurrentBuilder.Append(Node.GetUniqueName(this));
	}

	@Override public void VisitSetNameNode(SetNameNode Node) {
		this.VisitGetNameNode(Node.NameNode());
		this.CurrentBuilder.Append(" = ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitGetFieldNode(GetFieldNode Node) {
		this.GenerateSurroundCode(Node.RecvNode());
		this.CurrentBuilder.Append(".", Node.GetName());
	}

	@Override public void VisitSetFieldNode(SetFieldNode Node) {
		this.GenerateSurroundCode(Node.RecvNode());
		this.CurrentBuilder.Append(".", Node.GetName(), " = ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitMethodCallNode(MethodCallNode Node) {
		this.GenerateSurroundCode(Node.RecvNode());
		this.CurrentBuilder.Append(".", Node.MethodName());
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitMacroNode(BunMacroNode Node) {
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

	protected final void GenerateFuncName(BunFuncNameNode Node) {
		if(this.LangInfo.AllowFunctionOverloading) {
			this.CurrentBuilder.Append(Node.FuncName);
		}
		else {
			this.CurrentBuilder.Append(Node.GetSignature());
		}
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

	@Override public void VisitUnaryNode(UnaryOperatorNode Node) {
		this.CurrentBuilder.Append(Node.GetOperator());
		this.GenerateCode(null, Node.RecvNode());
	}

	@Override public void VisitPlusNode(BunPlusNode Node) {
		this.VisitUnaryNode(Node);
	}

	@Override public void VisitMinusNode(BunMinusNode Node) {
		this.VisitUnaryNode(Node);
	}

	@Override public void VisitComplementNode(BunComplementNode Node) {
		this.VisitUnaryNode(Node);
	}

	@Override public void VisitNotNode(BunNotNode Node) {
		this.CurrentBuilder.Append(this.NotOperator);
		this.GenerateSurroundCode(Node.RecvNode());
	}

	@Override public void VisitCastNode(BunCastNode Node) {
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

	@Override public void VisitInstanceOfNode(BInstanceOfNode Node) {
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.Append(" instanceof ");
		this.GenerateTypeName(Node.TargetType());
	}

	@Override public void VisitBinaryNode(BinaryOperatorNode Node) {
		if (Node.ParentNode instanceof BinaryOperatorNode) {
			this.CurrentBuilder.Append("(");
		}
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.AppendToken(Node.GetOperator());
		this.GenerateCode(null, Node.RightNode());
		if (Node.ParentNode instanceof BinaryOperatorNode) {
			this.CurrentBuilder.Append(")");
		}
	}

	@Override public void VisitAddNode(BunAddNode Node) {
		this.VisitBinaryNode(Node);
	}

	@Override public void VisitSubNode(BunSubNode Node) {
		this.VisitBinaryNode(Node);
	}

	@Override public void VisitMulNode(BunMulNode Node) {
		this.VisitBinaryNode(Node);
	}

	@Override public void VisitDivNode(BunDivNode Node) {
		this.VisitBinaryNode(Node);
	}

	@Override public void VisitModNode(BunModNode Node) {
		this.VisitBinaryNode(Node);
	}

	@Override public void VisitLeftShiftNode(BunLeftShiftNode Node) {
		this.VisitBinaryNode(Node);
	}

	@Override public void VisitRightShiftNode(BunRightShiftNode Node) {
		this.VisitBinaryNode(Node);
	}

	@Override public void VisitBitwiseAndNode(BunBitwiseAndNode Node) {
		this.VisitBinaryNode(Node);
	}

	@Override public void VisitBitwiseOrNode(BunBitwiseOrNode Node) {
		this.VisitBinaryNode(Node);
	}

	@Override public void VisitBitwiseXorNode(BunBitwiseXorNode Node) {
		this.VisitBinaryNode(Node);
	}

	protected void VisitComparatorNode(ComparatorNode Node) {
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.AppendToken(Node.GetOperator());
		this.GenerateCode(null, Node.RightNode());
	}

	@Override public void VisitEqualsNode(BunEqualsNode Node) {
		this.VisitComparatorNode(Node);
	}

	@Override public void VisitNotEqualsNode(BunNotEqualsNode Node) {
		this.VisitComparatorNode(Node);
	}

	@Override public void VisitLessThanNode(BunLessThanNode Node) {
		this.VisitComparatorNode(Node);
	}

	@Override public void VisitLessThanEqualsNode(BunLessThanEqualsNode Node) {
		this.VisitComparatorNode(Node);
	}

	@Override public void VisitGreaterThanNode(BunGreaterThanNode Node) {
		this.VisitComparatorNode(Node);
	}

	@Override public void VisitGreaterThanEqualsNode(BunGreaterThanEqualsNode Node) {
		this.VisitComparatorNode(Node);
	}

	@Override public void VisitLiteralNode(LiteralNode Node) {
		// TODO Auto-generated method stub
	}

	@Override public void VisitAndNode(BunAndNode Node) {
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.AppendToken(this.AndOperator);
		this.GenerateCode(null, Node.RightNode());
	}

	@Override public void VisitOrNode(BunOrNode Node) {
		this.GenerateCode(null, Node.LeftNode());
		this.CurrentBuilder.AppendToken(this.OrOperator);
		this.GenerateCode(null, Node.RightNode());
	}

	@Override public void VisitIfNode(BunIfNode Node) {
		this.GenerateCode2("if (", null, Node.CondNode(), ")");
		this.GenerateCode(null, Node.ThenNode());
		if (Node.HasElseNode()) {
			this.CurrentBuilder.AppendNewLine();
			this.CurrentBuilder.Append("else ");
			this.GenerateCode(null, Node.ElseNode());
		}
	}

	@Override public void VisitReturnNode(BunReturnNode Node) {
		this.CurrentBuilder.Append("return");
		if (Node.HasReturnExpr()) {
			this.CurrentBuilder.Append(" ");
			this.GenerateCode(null, Node.ExprNode());
		}
	}

	@Override public void VisitWhileNode(BunWhileNode Node) {
		this.GenerateCode2("while (", null, Node.CondNode(),")");
		this.GenerateCode(null, Node.BlockNode());
	}

	@Override public void VisitBreakNode(BunBreakNode Node) {
		this.CurrentBuilder.Append("break");
	}

	@Override public void VisitThrowNode(BunThrowNode Node) {
		this.CurrentBuilder.Append("throw ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitTryNode(BunTryNode Node) {
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

	@Override public void VisitLetNode(BunLetVarNode Node) {
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

	protected void VisitParamNode(BunLetVarNode Node) {
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
		this.GenerateTypeAnnotation(Node.Type);
	}

	protected void VisitFuncParamNode(String OpenToken, BunFunctionNode VargNode, String CloseToken) {
		this.CurrentBuilder.Append(OpenToken);
		@Var int i = 0;
		while(i < VargNode.GetListSize()) {
			@Var BunLetVarNode ParamNode = VargNode.GetParamNode(i);
			if (i > 0) {
				this.CurrentBuilder.Append(this.Camma);
			}
			this.VisitParamNode(ParamNode);
			i = i + 1;
		}
		this.CurrentBuilder.Append(CloseToken);
	}

	@Override public void VisitFunctionNode(BunFunctionNode Node) {
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

	@Override public void VisitClassNode(BunClassNode Node) {
		this.CurrentBuilder.AppendNewLine("class ", Node.ClassName());
		if(Node.SuperType() != null) {
			this.CurrentBuilder.Append(" extends ");
			this.GenerateTypeName(Node.SuperType());
		}
		this.CurrentBuilder.OpenIndent(" {");
		@Var int i = 0;
		while (i < Node.GetListSize()) {
			@Var BunLetVarNode FieldNode = Node.GetFieldNode(i);
			this.CurrentBuilder.AppendNewLine("var ", FieldNode.GetGivenName());
			this.GenerateTypeAnnotation(FieldNode.DeclType());
			this.CurrentBuilder.AppendToken("=");
			this.GenerateCode(null, FieldNode.InitValueNode());
			this.CurrentBuilder.Append(this.SemiColon);
			i = i + 1;
		}
		this.CurrentBuilder.CloseIndent("}");
	}

	@Override public void VisitErrorNode(ErrorNode Node) {
		@Var String Message = BLogger._LogError(Node.SourceToken, Node.ErrorMessage);
		this.CurrentBuilder.Append(this.ErrorFunc, "(");
		this.CurrentBuilder.Append(BLib._QuoteString(Message));
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitAsmNode(BunAsmNode Node) {
		this.ImportLibrary(Node.RequiredLibrary);
		this.CurrentBuilder.AppendCode(Node.GetMacroText());
	}

	@Override public void VisitLocalDefinedNode(LocalDefinedNode Node) {
		this.VisitUndefinedNode(Node);
	}

	@Override public void VisitTopLevelNode(TopLevelNode Node) {
		this.VisitUndefinedNode(Node);
	}

	@Override public void VisitSyntaxSugarNode(SyntaxSugarNode Node) {
		@Var DesugarNode DesugarNode = Node.DeSugar(this, this.TypeChecker);
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

	protected void VisitListNode(String OpenToken, AbstractListNode VargNode, String DelimToken, String CloseToken) {
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

	protected void VisitListNode(String OpenToken, AbstractListNode VargNode, String CloseToken) {
		this.VisitListNode(OpenToken, VargNode, this.Camma, CloseToken);
	}

	protected void GenerateWrapperCall(String OpenToken, BunFunctionNode FuncNode, String CloseToken) {
		this.CurrentBuilder.Append(OpenToken);
		@Var int i = 0;
		while(i < FuncNode.GetListSize()) {
			@Var BunLetVarNode ParamNode = FuncNode.GetParamNode(i);
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
