package libbun.encode;

import libbun.parser.BLogger;
import libbun.parser.ast.BLetVarNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.ZArrayLiteralNode;
import libbun.parser.ast.ZClassNode;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.ZFuncCallNode;
import libbun.parser.ast.ZFuncNameNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.ZGetIndexNode;
import libbun.parser.ast.ZInstanceOfNode;
import libbun.parser.ast.ZMapEntryNode;
import libbun.parser.ast.ZMapLiteralNode;
import libbun.parser.ast.ZMethodCallNode;
import libbun.parser.ast.ZNewObjectNode;
import libbun.parser.ast.ZSetIndexNode;
import libbun.parser.ast.ZThrowNode;
import libbun.parser.ast.ZTryNode;
import libbun.type.BClassField;
import libbun.type.BClassType;
import libbun.type.BFuncType;
import libbun.type.BType;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.Var;
import libbun.util.BArray;
import libbun.util.BMap;
import libbun.util.ZenMethod;

public class JavaGenerator extends ZSourceGenerator {

	@BField private ZFunctionNode MainFuncNode = null;
	@BField private final BArray<ZFunctionNode> ExportFunctionList = new BArray<ZFunctionNode>(new ZFunctionNode[4]);

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
		this.CurrentBuilder.AppendNewLine("/* end of header */", this.LineFeed);
	}

	@Override protected void GenerateImportLibrary(String LibName) {
		this.HeaderBuilder.AppendNewLine("import ", LibName, this.SemiColon);
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
		this.CurrentBuilder.OpenIndent(" {");
		if(this.MainFuncNode != null) {
			this.CurrentBuilder.AppendNewLine("public final static void main(String[] a)");
			this.CurrentBuilder.OpenIndent(" {");
			this.CurrentBuilder.AppendNewLine(this.NameFunctionClass(this.MainFuncNode.FuncName(), BType.VoidType, 0), ".f();");
			this.CurrentBuilder.CloseIndent("}");
		}
		this.CurrentBuilder.CloseIndent("}");
		this.CurrentBuilder.AppendLineFeed();
	}


	@Override protected void GenerateCode(BType ContextType, BNode Node) {
		if(Node.IsUntyped() && !Node.IsErrorNode() && !(Node instanceof ZFuncNameNode)) {
			BLogger._LogError(Node.SourceToken, "untyped error: " + Node);
			Node.Accept(this);
			this.CurrentBuilder.Append("/*untyped*/");
		}
		else {
			if(ContextType != null && Node.Type != ContextType && !ContextType.IsGreekType()) {
				this.CurrentBuilder.Append("(");
				this.GenerateTypeName(ContextType);
				this.CurrentBuilder.Append(")");
			}
			Node.Accept(this);
		}
	}

	@Override public void VisitArrayLiteralNode(ZArrayLiteralNode Node) {
		if(Node.GetListSize() == 0) {
			this.CurrentBuilder.Append("new ", this.GetJavaTypeName(Node.Type, false), "()");
		}
		else {
			@Var BType ParamType = Node.Type.GetParamType(0);
			this.ImportLibrary("java.util.Arrays");
			this.CurrentBuilder.Append("new ", this.GetJavaTypeName(Node.Type, false), "(");
			this.CurrentBuilder.Append("Arrays.asList(new ", this.GetJavaTypeName(ParamType, true), "[]");
			this.VisitListNode("{", Node, "}))");
		}
	}

	@Override public void VisitMapLiteralNode(ZMapLiteralNode Node) {
		this.CurrentBuilder.Append("new ", this.GetJavaTypeName(Node.Type, false), "()");
		if(Node.GetListSize() > 0) {
			@Var int i = 0;
			this.CurrentBuilder.OpenIndent(" {{");
			while(i < Node.GetListSize()) {
				@Var ZMapEntryNode Entry = Node.GetMapEntryNode(i);
				this.CurrentBuilder.AppendNewLine("put");
				this.GenerateCode2("(", Entry.KeyNode(), this.Camma, Entry.ValueNode(), ");");
				i = i + 1;
			}
			this.CurrentBuilder.CloseIndent("}}");
		}
	}

	@Override public void VisitNewObjectNode(ZNewObjectNode Node) {
		this.CurrentBuilder.Append("new " + this.NameClass(Node.Type));
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitGetIndexNode(ZGetIndexNode Node) {
		@Var BType RecvType = Node.GetAstType(ZGetIndexNode._Recv);
		if(RecvType.IsStringType()) {
			this.GenerateCode2("String.valueOf((", null, Node.RecvNode(), ")");
			this.GenerateCode2(".charAt(", null, Node.IndexNode(), "))");
		}
		else {
			this.GenerateCode(null, Node.RecvNode());
			this.GenerateCode2(".get(", null, Node.IndexNode(), ")");
		}
	}

	@Override public void VisitSetIndexNode(ZSetIndexNode Node) {
		@Var BType RecvType = Node.GetAstType(ZGetIndexNode._Recv);
		this.GenerateCode(null, Node.RecvNode());
		if(RecvType.IsMapType()) {
			this.CurrentBuilder.Append(".put(");
		}
		else {
			this.CurrentBuilder.Append(".set(");
		}
		this.GenerateCode(null, Node.IndexNode());
		this.CurrentBuilder.Append(this.Camma);
		this.GenerateCode(null, Node.ExprNode());
		this.CurrentBuilder.Append(")");
	}

	@Override public void VisitMethodCallNode(ZMethodCallNode Node) {
		this.GenerateSurroundCode(Node.RecvNode());
		this.CurrentBuilder.Append(".");
		this.CurrentBuilder.Append(Node.MethodName());
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitFuncCallNode(ZFuncCallNode Node) {
		@Var ZFuncNameNode FuncNameNode = Node.FuncNameNode();
		if(FuncNameNode != null) {
			this.CurrentBuilder.Append(this.NameFunctionClass(FuncNameNode.FuncName, FuncNameNode.RecvType, FuncNameNode.FuncParamSize), ".f");
		}
		else {
			this.GenerateCode(null, Node.FunctorNode());
			this.CurrentBuilder.Append(".Invoke");
		}
		this.VisitListNode("(", Node, ")");
	}

	//	@Override public void VisitCastNode(ZCastNode Node) {
	//		this.CurrentBuilder.Append("(");
	//		this.VisitType(Node.Type);
	//		this.CurrentBuilder.Append(")");
	//		this.GenerateSurroundCode(Node.ExprNode());
	//	}

	@Override public void VisitThrowNode(ZThrowNode Node) {
		this.CurrentBuilder.Append("throw ");
		this.GenerateCode2("new RuntimeException((", null, Node.ExprNode(),").toString())");
	}

	@Override public void VisitTryNode(ZTryNode Node) {
		this.CurrentBuilder.Append("try ");
		this.GenerateCode(null, Node.TryBlockNode());
		if(Node.HasCatchBlockNode()) {
			@Var String VarName = this.NameUniqueSymbol("e");
			this.CurrentBuilder.AppendNewLine("catch (Exception ", VarName, ")");
			this.CurrentBuilder.OpenIndent(" {");
			this.CurrentBuilder.AppendNewLine("Object ", Node.ExceptionName(), " = ");
			this.CurrentBuilder.Append("/*FIXME*/", VarName, this.SemiColon);
			this.VisitStmtList(Node.CatchBlockNode());
			this.CurrentBuilder.Append(this.SemiColon);
			this.CurrentBuilder.CloseIndent("}");
		}
		if(Node.HasFinallyBlockNode()) {
			this.CurrentBuilder.AppendNewLine("finally ");
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

			this.CurrentBuilder = this.InsertNewSourceBuilder();
			this.CurrentBuilder.AppendNewLine("abstract class ", ClassName, "");
			this.CurrentBuilder.OpenIndent(" {");
			this.CurrentBuilder.AppendNewLine("abstract ");
			this.GenerateTypeName(FuncType.GetReturnType());
			this.CurrentBuilder.Append(" Invoke(");
			@Var int i = 0;
			while(i < FuncType.GetFuncParamSize()) {
				if(i > 0) {
					this.CurrentBuilder.Append(this.Camma);
				}
				this.GenerateTypeName(FuncType.GetFuncParamType(i));
				this.CurrentBuilder.Append(" x"+i);
				i = i + 1;
			}
			this.CurrentBuilder.Append(");");

			this.CurrentBuilder.AppendNewLine(ClassName, "(int TypeId, String Name)");
			this.CurrentBuilder.OpenIndent(" {");
			this.CurrentBuilder.AppendNewLine("//super(TypeId, Name);");
			this.CurrentBuilder.CloseIndent("}");
			this.CurrentBuilder.CloseIndent("}");
			this.CurrentBuilder.AppendNewLine();
			this.CurrentBuilder = this.CurrentBuilder.Pop();
		}
		return ClassName;
	}

	@Override protected void GenerateTypeName(BType Type) {
		if(Type instanceof BFuncType) {
			this.CurrentBuilder.Append(this.GetFuncTypeClass((BFuncType)Type));
		}
		else {
			this.CurrentBuilder.Append(this.GetJavaTypeName(Type.GetRealType(), false));
		}
	}

	@Override
	protected void VisitVarDeclNode(BLetVarNode Node) {
		this.GenerateTypeName(Node.DeclType());
		this.CurrentBuilder.Append(" ");
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
		this.CurrentBuilder.Append(" = ");
		this.GenerateCode(null, Node.InitValueNode());
		this.CurrentBuilder.Append(this.SemiColon);
		if(Node.HasNextVarNode()) { this.VisitVarDeclNode(Node.NextVarNode()); }
	}

	@Override public void VisitLetNode(BLetVarNode Node) {
		@Var String ClassName = this.NameGlobalNameClass(Node.GetUniqueName(this));
		//		this.CurrentBuilder = this.InsertNewSourceBuilder();
		this.CurrentBuilder.AppendNewLine("final class ", ClassName, "");
		this.CurrentBuilder.OpenIndent(" {");
		this.GenerateClassField("static", Node.GetAstType(BLetVarNode._InitValue), "_", null);
		this.GenerateCode2(" = ", null, Node.InitValueNode(), this.SemiColon);
		this.CurrentBuilder.CloseIndent("}");
		//		this.CurrentBuilder = this.CurrentBuilder.Pop();
		//			Node.GlobalName = ClassName + "._";
		//			Node.GetNameSpace().SetLocalSymbol(Node.GetName(), Node.ToGlobalNameNode());
	}

	@Override protected void VisitParamNode(BLetVarNode Node) {
		this.GenerateTypeName(Node.Type);
		this.CurrentBuilder.Append(" ");
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
	}

	@Override public void VisitFunctionNode(ZFunctionNode Node) {
		if(!Node.Type.IsVoidType()) {
			@Var String FuncName = Node.GetUniqueName(this);
			this.CurrentBuilder = this.InsertNewSourceBuilder();
			FuncName = this.GenerateFunctionAsClass(FuncName, Node);
			this.CurrentBuilder.AppendLineFeed();
			this.CurrentBuilder = this.CurrentBuilder.Pop();
			this.CurrentBuilder.Append(FuncName);
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

	private String GenerateFunctionAsClass(String FuncName, ZFunctionNode Node) {
		@Var BFuncType FuncType = Node.GetFuncType();
		@Var String ClassName = this.NameFunctionClass(FuncName, FuncType);
		this.GenerateClass("final", ClassName, FuncType);
		this.CurrentBuilder.OpenIndent(" {");
		this.CurrentBuilder.AppendNewLine("static ");
		this.GenerateTypeName(Node.ReturnType());
		this.CurrentBuilder.Append(" f");
		this.VisitFuncParamNode("(", Node, ")");
		this.GenerateCode(null, Node.BlockNode());

		this.GenerateClassField("static ", FuncType, "function", "new " + ClassName + "();");
		this.CurrentBuilder.AppendNewLine(ClassName, "()");
		this.CurrentBuilder.OpenIndent(" {");

		this.CurrentBuilder.AppendNewLine("super(", ""+FuncType.TypeId, this.Camma);
		this.CurrentBuilder.Append(BLib._QuoteString(FuncName), ");");
		this.CurrentBuilder.CloseIndent("}");

		this.CurrentBuilder.AppendNewLine("");
		this.GenerateTypeName(Node.ReturnType());
		this.CurrentBuilder.Append(" Invoke");
		this.VisitListNode("(", Node, ")");
		this.CurrentBuilder.OpenIndent(" {");
		if(!FuncType.GetReturnType().IsVoidType()) {
			this.CurrentBuilder.AppendNewLine("return ", ClassName, ".f");
		}
		else {
			this.CurrentBuilder.AppendNewLine(ClassName, ".f");
		}
		this.GenerateWrapperCall("(", Node, ");");
		this.CurrentBuilder.CloseIndent("}");

		this.CurrentBuilder.CloseIndent("}");
		this.CurrentBuilder.AppendNewLine();
		return ClassName + ".function";
	}

	@Override public void VisitInstanceOfNode(ZInstanceOfNode Node) {
		this.GenerateCode(null, Node.AST[ZInstanceOfNode._Left]);
		this.CurrentBuilder.Append(" instanceof ");
		this.GenerateTypeName(Node.TargetType());
	}


	private void GenerateClass(String Qualifier, String ClassName, BType SuperType) {
		if(Qualifier != null && Qualifier.length() > 0) {
			this.CurrentBuilder.AppendNewLine(Qualifier);
			this.CurrentBuilder.AppendWhiteSpace("class ", ClassName);
		}
		else {
			this.CurrentBuilder.AppendNewLine("class ", ClassName);
		}
		if(!SuperType.Equals(BClassType._ObjectType)) {
			this.CurrentBuilder.Append(" extends ");
			this.GenerateTypeName(SuperType);
		}
	}

	private void GenerateClassField(String Qualifier, BType FieldType, String FieldName, String Value) {
		this.CurrentBuilder.AppendNewLine(Qualifier);
		this.CurrentBuilder.AppendWhiteSpace();
		this.GenerateTypeName(FieldType);
		this.CurrentBuilder.Append(" ", FieldName);
		if(Value != null) {
			this.CurrentBuilder.Append(" = ", Value);
			this.CurrentBuilder.Append(this.SemiColon);
		}
	}

	@Override public void VisitClassNode(ZClassNode Node) {
		@Var BType SuperType = Node.ClassType.GetSuperType();
		@Var String ClassName = this.NameClass(Node.ClassType);
		this.GenerateClass("", ClassName, SuperType);
		this.CurrentBuilder.OpenIndent(" {");
		@Var int i = 0;
		while (i < Node.GetListSize()) {
			@Var BLetVarNode FieldNode = Node.GetFieldNode(i);
			this.GenerateClassField("", FieldNode.DeclType(), FieldNode.GetGivenName(), null);
			this.CurrentBuilder.Append(this.SemiColon);
			i = i + 1;
		}
		this.CurrentBuilder.AppendNewLine();

		i = 0;
		while(i < Node.ClassType.GetFieldSize()) {
			@Var BClassField Field = Node.ClassType.GetFieldAt(i);
			if(Field.FieldType.IsFuncType()) {
				this.GenerateClassField("static", Field.FieldType, this.NameMethod(Node.ClassType, Field.FieldName), "null");
				this.CurrentBuilder.Append(this.SemiColon);
			}
			i = i + 1;
		}

		this.CurrentBuilder.AppendNewLine(this.NameClass(Node.ClassType), "()");
		this.CurrentBuilder.OpenIndent(" {");
		this.CurrentBuilder.AppendNewLine("super();");
		while (i < Node.GetListSize()) {
			@Var BLetVarNode FieldNode = Node.GetFieldNode(i);
			this.CurrentBuilder.AppendNewLine("this.", FieldNode.GetGivenName(), "=");
			this.GenerateCode(null, FieldNode.InitValueNode());
			this.CurrentBuilder.Append(this.SemiColon);
			i = i + 1;
		}
		i = 0;
		while(i < Node.ClassType.GetFieldSize()) {
			@Var BClassField Field = Node.ClassType.GetFieldAt(i);
			if(Field.FieldType.IsFuncType()) {
				this.CurrentBuilder.AppendNewLine("if(", this.NameMethod(Node.ClassType, Field.FieldName), " != null) ");
				this.CurrentBuilder.OpenIndent("{");
				this.CurrentBuilder.AppendNewLine("this.", Field.FieldName, "=");
				this.CurrentBuilder.Append(this.NameMethod(Node.ClassType, Field.FieldName), ";", this.LineFeed);
				this.CurrentBuilder.CloseIndent("}");
			}
			i = i + 1;
		}

		this.CurrentBuilder.CloseIndent("}"); /* end of constructor*/
		this.CurrentBuilder.CloseIndent("}");  /* end of class */
		this.CurrentBuilder.AppendLineFeed();
	}

	@Override public void VisitErrorNode(ZErrorNode Node) {
		BLogger._LogError(Node.SourceToken, Node.ErrorMessage);
		this.CurrentBuilder.Append("ThrowError(");
		this.CurrentBuilder.Append(BLib._QuoteString(Node.ErrorMessage));
		this.CurrentBuilder.Append(")");
	}

	//	@Override public void VisitExtendedNode(ZNode Node) {
	//
	//	}


}
