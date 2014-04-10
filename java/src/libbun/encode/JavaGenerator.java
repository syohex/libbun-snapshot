package libbun.encode;

import libbun.ast.BNode;
import libbun.ast.binary.BInstanceOfNode;
import libbun.ast.decl.BunClassNode;
import libbun.ast.decl.BunFunctionNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.ast.error.ErrorNode;
import libbun.ast.expression.BunFuncNameNode;
import libbun.ast.expression.FuncCallNode;
import libbun.ast.expression.GetIndexNode;
import libbun.ast.expression.MethodCallNode;
import libbun.ast.expression.NewObjectNode;
import libbun.ast.expression.SetIndexNode;
import libbun.ast.literal.BunArrayLiteralNode;
import libbun.ast.literal.BunMapEntryNode;
import libbun.ast.literal.BunMapLiteralNode;
import libbun.ast.statement.BunThrowNode;
import libbun.ast.statement.BunTryNode;
import libbun.encode.obsolete.OldSourceGenerator;
import libbun.parser.BLogger;
import libbun.type.BClassField;
import libbun.type.BClassType;
import libbun.type.BFuncType;
import libbun.type.BType;
import libbun.util.BArray;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.BMap;
import libbun.util.Var;
import libbun.util.ZenMethod;

public class JavaGenerator extends OldSourceGenerator {

	@BField private BunFunctionNode MainFuncNode = null;
	@BField private final BArray<BunFunctionNode> ExportFunctionList = new BArray<BunFunctionNode>(new BunFunctionNode[4]);

	public JavaGenerator() {
		super("java", "1.6");
		this.IntLiteralSuffix="";
		this.TopType = "Object";
		this.SetNativeType(BType.BooleanType, "boolean");
		this.SetNativeType(BType.IntType, "int");  // for beautiful code
		this.SetNativeType(BType.FloatType, "double");
		this.SetNativeType(BType.StringType, "String");
		this.SetNativeType(BType.VarType, "Object");  // for safety

		this.SetReservedName("this", "self");

		//this.HeaderBuilder.AppendNewLine("import zen.util.*;");
		this.Source.AppendNewLine("/* end of header */", this.LineFeed);
	}

	@Override protected void GenerateImportLibrary(String LibName) {
		this.Header.AppendNewLine("import ", LibName, this.SemiColon);
	}

	@Override @ZenMethod protected void Finish(String FileName) {
		if(FileName == null) {
			FileName = "ZenMain";
		}
		else {
			@Var int loc = FileName.lastIndexOf('/');
			if(loc != -1) {
				FileName = FileName.substring(loc+1);
			}
		}
		this.GenerateClass("public final", FileName, BClassType._ObjectType);
		this.Source.OpenIndent(" {");
		if(this.MainFuncNode != null) {
			this.Source.AppendNewLine("public final static void main(String[] a)");
			this.Source.OpenIndent(" {");
			this.Source.AppendNewLine(this.NameFunctionClass(this.MainFuncNode.FuncName(), BType.VoidType, 0), ".f();");
			this.Source.CloseIndent("}");
		}
		this.Source.CloseIndent("}");
		this.Source.AppendLineFeed();
	}


	@Override protected void GenerateCode(BType ContextType, BNode Node) {
		if(Node.IsUntyped() && !Node.IsErrorNode() && !(Node instanceof BunFuncNameNode)) {
			BLogger._LogError(Node.SourceToken, "untyped error: " + Node);
			Node.Accept(this);
			this.Source.Append("/*untyped*/");
		}
		else {
			if(ContextType != null && Node.Type != ContextType && !ContextType.IsGreekType()) {
				this.Source.Append("(");
				this.GenerateTypeName(ContextType);
				this.Source.Append(")");
			}
			Node.Accept(this);
		}
	}

	@Override public void VisitArrayLiteralNode(BunArrayLiteralNode Node) {
		if(Node.GetListSize() == 0) {
			this.Source.Append("new ", this.GetJavaTypeName(Node.Type, false), "()");
		}
		else {
			@Var BType ParamType = Node.Type.GetParamType(0);
			this.ImportLibrary("java.util.Arrays");
			this.Source.Append("new ", this.GetJavaTypeName(Node.Type, false), "(");
			this.Source.Append("Arrays.asList(new ", this.GetJavaTypeName(ParamType, true), "[]");
			this.VisitListNode("{", Node, "}))");
		}
	}

	@Override public void VisitMapLiteralNode(BunMapLiteralNode Node) {
		this.Source.Append("new ", this.GetJavaTypeName(Node.Type, false), "()");
		if(Node.GetListSize() > 0) {
			@Var int i = 0;
			this.Source.OpenIndent(" {{");
			while(i < Node.GetListSize()) {
				@Var BunMapEntryNode Entry = Node.GetMapEntryNode(i);
				this.Source.AppendNewLine("put");
				this.GenerateCode2("(", Entry.KeyNode(), this.Camma, Entry.ValueNode(), ");");
				i = i + 1;
			}
			this.Source.CloseIndent("}}");
		}
	}

	@Override public void VisitNewObjectNode(NewObjectNode Node) {
		this.Source.Append("new " + this.NameClass(Node.Type));
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitGetIndexNode(GetIndexNode Node) {
		@Var BType RecvType = Node.GetAstType(GetIndexNode._Recv);
		if(RecvType.IsStringType()) {
			this.GenerateCode2("String.valueOf((", null, Node.RecvNode(), ")");
			this.GenerateCode2(".charAt(", null, Node.IndexNode(), "))");
		}
		else {
			this.GenerateCode(null, Node.RecvNode());
			this.GenerateCode2(".get(", null, Node.IndexNode(), ")");
		}
	}

	@Override public void VisitSetIndexNode(SetIndexNode Node) {
		@Var BType RecvType = Node.GetAstType(GetIndexNode._Recv);
		this.GenerateCode(null, Node.RecvNode());
		if(RecvType.IsMapType()) {
			this.Source.Append(".put(");
		}
		else {
			this.Source.Append(".set(");
		}
		this.GenerateCode(null, Node.IndexNode());
		this.Source.Append(this.Camma);
		this.GenerateCode(null, Node.ExprNode());
		this.Source.Append(")");
	}

	@Override public void VisitMethodCallNode(MethodCallNode Node) {
		this.GenerateSurroundCode(Node.RecvNode());
		this.Source.Append(".");
		this.Source.Append(Node.MethodName());
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitFuncCallNode(FuncCallNode Node) {
		@Var BunFuncNameNode FuncNameNode = Node.FuncNameNode();
		if(FuncNameNode != null) {
			this.Source.Append(this.NameFunctionClass(FuncNameNode.FuncName, FuncNameNode.RecvType, FuncNameNode.FuncParamSize), ".f");
		}
		else {
			this.GenerateCode(null, Node.FunctorNode());
			this.Source.Append(".Invoke");
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
		this.Source.Append("throw ");
		this.GenerateCode2("new RuntimeException((", null, Node.ExprNode(),").toString())");
	}

	@Override public void VisitTryNode(BunTryNode Node) {
		this.Source.Append("try ");
		this.GenerateCode(null, Node.TryBlockNode());
		if(Node.HasCatchBlockNode()) {
			@Var String VarName = this.NameUniqueSymbol("e");
			this.Source.AppendNewLine("catch (Exception ", VarName, ")");
			this.Source.OpenIndent(" {");
			this.Source.AppendNewLine("Object ", Node.ExceptionName(), " = ");
			this.Source.Append("/*FIXME*/", VarName, this.SemiColon);
			this.VisitStmtList(Node.CatchBlockNode());
			this.Source.Append(this.SemiColon);
			this.Source.CloseIndent("}");
		}
		if(Node.HasFinallyBlockNode()) {
			this.Source.AppendNewLine("finally ");
			this.GenerateCode(null, Node.FinallyBlockNode());
		}
	}

	private String GetJavaTypeName(BType Type, boolean Boxing) {
		if(Type.IsArrayType()) {
			this.ImportLibrary("java.util.ArrayList");
			return "ArrayList<" + this.GetJavaTypeName(Type.GetParamType(0), true) + ">";
		}
		if(Type.IsMapType()) {
			this.ImportLibrary("java.util.HashMap");
			return "HashMap<String," + this.GetJavaTypeName(Type.GetParamType(0), true) + ">";
		}
		if(Type instanceof BFuncType) {
			return this.GetFuncTypeClass((BFuncType)Type);
		}
		if(Type instanceof BClassType) {
			return this.NameClass(Type);
		}
		if(Boxing) {
			if(Type.IsIntType()) {
				return "Integer";
			}
			if(Type.IsFloatType()) {
				return "Double";
			}
			if(Type.IsBooleanType()) {
				return "Boolean";
			}
		}
		return this.GetNativeTypeName(Type);
	}


	@BField private final BMap<String> FuncNameMap = new BMap<String>(null);

	String GetFuncTypeClass(BFuncType FuncType) {
		@Var String ClassName = this.FuncNameMap.GetOrNull(FuncType.GetUniqueName());
		if(ClassName == null) {
			ClassName = this.NameType(FuncType);
			this.FuncNameMap.put(FuncType.GetUniqueName(), ClassName);

			this.Source = this.InsertNewSourceBuilder();
			this.Source.AppendNewLine("abstract class ", ClassName, "");
			this.Source.OpenIndent(" {");
			this.Source.AppendNewLine("abstract ");
			this.GenerateTypeName(FuncType.GetReturnType());
			this.Source.Append(" Invoke(");
			@Var int i = 0;
			while(i < FuncType.GetFuncParamSize()) {
				if(i > 0) {
					this.Source.Append(this.Camma);
				}
				this.GenerateTypeName(FuncType.GetFuncParamType(i));
				this.Source.Append(" x"+i);
				i = i + 1;
			}
			this.Source.Append(");");

			this.Source.AppendNewLine(ClassName, "(int TypeId, String Name)");
			this.Source.OpenIndent(" {");
			this.Source.AppendNewLine("//super(TypeId, Name);");
			this.Source.CloseIndent("}");
			this.Source.CloseIndent("}");
			this.Source.AppendNewLine();
			this.Source = this.Source.Pop();
		}
		return ClassName;
	}

	@Override protected void GenerateTypeName(BType Type) {
		if(Type instanceof BFuncType) {
			this.Source.Append(this.GetFuncTypeClass((BFuncType)Type));
		}
		else {
			this.Source.Append(this.GetJavaTypeName(Type.GetRealType(), false));
		}
	}

	@Override
	protected void VisitVarDeclNode(BunLetVarNode Node) {
		this.GenerateTypeName(Node.DeclType());
		this.Source.Append(" ");
		this.Source.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
		this.Source.Append(" = ");
		this.GenerateCode(null, Node.InitValueNode());
		this.Source.Append(this.SemiColon);
		if(Node.HasNextVarNode()) { this.VisitVarDeclNode(Node.NextVarNode()); }
	}

	@Override public void VisitLetNode(BunLetVarNode Node) {
		@Var String ClassName = this.NameGlobalNameClass(Node.GetUniqueName(this));
		//		this.CurrentBuilder = this.InsertNewSourceBuilder();
		this.Source.AppendNewLine("final class ", ClassName, "");
		this.Source.OpenIndent(" {");
		this.GenerateClassField("static", Node.GetAstType(BunLetVarNode._InitValue), "_", null);
		this.GenerateCode2(" = ", null, Node.InitValueNode(), this.SemiColon);
		this.Source.CloseIndent("}");
		//		this.CurrentBuilder = this.CurrentBuilder.Pop();
		//			Node.GlobalName = ClassName + "._";
		//			Node.GetNameSpace().SetLocalSymbol(Node.GetName(), Node.ToGlobalNameNode());
	}

	@Override protected void VisitParamNode(BunLetVarNode Node) {
		this.GenerateTypeName(Node.Type);
		this.Source.Append(" ");
		this.Source.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
	}

	@Override public void VisitFunctionNode(BunFunctionNode Node) {
		if(!Node.Type.IsVoidType()) {
			@Var String FuncName = Node.GetUniqueName(this);
			this.Source = this.InsertNewSourceBuilder();
			FuncName = this.GenerateFunctionAsClass(FuncName, Node);
			this.Source.AppendLineFeed();
			this.Source = this.Source.Pop();
			this.Source.Append(FuncName);
		}
		else {
			this.GenerateFunctionAsClass(Node.FuncName(), Node);
			if(Node.IsExport) {
				if(Node.FuncName().equals("main")) {
					this.MainFuncNode = Node;
				}
				else {
					this.ExportFunctionList.add(Node);
				}
			}
			//@Var ZFuncType FuncType = Node.GetFuncType();
			//			if(this.IsMethod(Node.FuncName, FuncType)) {
			//				this.HeaderBuilder.Append("#define _" + this.NameMethod(FuncType.GetRecvType(), Node.FuncName));
			//				this.HeaderBuilder.AppendLineFeed();
			//			}
		}
	}

	private String GenerateFunctionAsClass(String FuncName, BunFunctionNode Node) {
		@Var BFuncType FuncType = Node.GetFuncType();
		@Var String ClassName = this.NameFunctionClass(FuncName, FuncType);
		this.GenerateClass("final", ClassName, FuncType);
		this.Source.OpenIndent(" {");
		this.Source.AppendNewLine("static ");
		this.GenerateTypeName(Node.ReturnType());
		this.Source.Append(" f");
		this.VisitFuncParamNode("(", Node, ")");
		this.GenerateCode(null, Node.BlockNode());

		this.GenerateClassField("static ", FuncType, "function", "new " + ClassName + "();");
		this.Source.AppendNewLine(ClassName, "()");
		this.Source.OpenIndent(" {");

		this.Source.AppendNewLine("super(", ""+FuncType.TypeId, this.Camma);
		this.Source.Append(BLib._QuoteString(FuncName), ");");
		this.Source.CloseIndent("}");

		this.Source.AppendNewLine("");
		this.GenerateTypeName(Node.ReturnType());
		this.Source.Append(" Invoke");
		this.VisitListNode("(", Node, ")");
		this.Source.OpenIndent(" {");
		if(!FuncType.GetReturnType().IsVoidType()) {
			this.Source.AppendNewLine("return ", ClassName, ".f");
		}
		else {
			this.Source.AppendNewLine(ClassName, ".f");
		}
		this.GenerateWrapperCall("(", Node, ");");
		this.Source.CloseIndent("}");

		this.Source.CloseIndent("}");
		this.Source.AppendNewLine();
		return ClassName + ".function";
	}

	@Override public void VisitInstanceOfNode(BInstanceOfNode Node) {
		this.GenerateCode(null, Node.AST[BInstanceOfNode._Left]);
		this.Source.Append(" instanceof ");
		this.GenerateTypeName(Node.TargetType());
	}


	private void GenerateClass(String Qualifier, String ClassName, BType SuperType) {
		if(Qualifier != null && Qualifier.length() > 0) {
			this.Source.AppendNewLine(Qualifier);
			this.Source.AppendWhiteSpace("class");
			this.Source.Append(ClassName);
		}
		else {
			this.Source.AppendNewLine("class ", ClassName);
		}
		if(!SuperType.Equals(BClassType._ObjectType)) {
			this.Source.Append(" extends ");
			this.GenerateTypeName(SuperType);
		}
	}

	private void GenerateClassField(String Qualifier, BType FieldType, String FieldName, String Value) {
		this.Source.AppendNewLine(Qualifier);
		this.Source.AppendWhiteSpace();
		this.GenerateTypeName(FieldType);
		this.Source.Append(" ", FieldName);
		if(Value != null) {
			this.Source.Append(" = ", Value);
			this.Source.Append(this.SemiColon);
		}
	}

	@Override public void VisitClassNode(BunClassNode Node) {
		@Var BType SuperType = Node.ClassType.GetSuperType();
		@Var String ClassName = this.NameClass(Node.ClassType);
		this.GenerateClass("", ClassName, SuperType);
		this.Source.OpenIndent(" {");
		@Var int i = 0;
		while (i < Node.GetListSize()) {
			@Var BunLetVarNode FieldNode = Node.GetFieldNode(i);
			this.GenerateClassField("", FieldNode.DeclType(), FieldNode.GetGivenName(), null);
			this.Source.Append(this.SemiColon);
			i = i + 1;
		}
		this.Source.AppendNewLine();

		i = 0;
		while(i < Node.ClassType.GetFieldSize()) {
			@Var BClassField Field = Node.ClassType.GetFieldAt(i);
			if(Field.FieldType.IsFuncType()) {
				this.GenerateClassField("static", Field.FieldType, this.NameMethod(Node.ClassType, Field.FieldName), "null");
				this.Source.Append(this.SemiColon);
			}
			i = i + 1;
		}

		this.Source.AppendNewLine(this.NameClass(Node.ClassType), "()");
		this.Source.OpenIndent(" {");
		this.Source.AppendNewLine("super();");
		while (i < Node.GetListSize()) {
			@Var BunLetVarNode FieldNode = Node.GetFieldNode(i);
			this.Source.AppendNewLine("this.", FieldNode.GetGivenName(), "=");
			this.GenerateCode(null, FieldNode.InitValueNode());
			this.Source.Append(this.SemiColon);
			i = i + 1;
		}
		i = 0;
		while(i < Node.ClassType.GetFieldSize()) {
			@Var BClassField Field = Node.ClassType.GetFieldAt(i);
			if(Field.FieldType.IsFuncType()) {
				this.Source.AppendNewLine("if(", this.NameMethod(Node.ClassType, Field.FieldName), " != null) ");
				this.Source.OpenIndent("{");
				this.Source.AppendNewLine("this.", Field.FieldName, "=");
				this.Source.Append(this.NameMethod(Node.ClassType, Field.FieldName), ";", this.LineFeed);
				this.Source.CloseIndent("}");
			}
			i = i + 1;
		}

		this.Source.CloseIndent("}"); /* end of constructor*/
		this.Source.CloseIndent("}");  /* end of class */
		this.Source.AppendLineFeed();
	}

	@Override public void VisitErrorNode(ErrorNode Node) {
		BLogger._LogError(Node.SourceToken, Node.ErrorMessage);
		this.Source.Append("ThrowError(");
		this.Source.Append(BLib._QuoteString(Node.ErrorMessage));
		this.Source.Append(")");
	}

	//	@Override public void VisitExtendedNode(ZNode Node) {
	//
	//	}


}
