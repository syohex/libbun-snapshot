package libbun.encode;

import libbun.ast.BNode;
import libbun.ast.binary.BInstanceOfNode;
import libbun.ast.decl.BunClassNode;
import libbun.ast.decl.BunFunctionNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.ast.error.ErrorNode;
import libbun.ast.expression.BunFuncNameNode;
import libbun.ast.expression.GetIndexNode;
import libbun.ast.expression.MethodCallNode;
import libbun.ast.expression.NewObjectNode;
import libbun.ast.expression.SetIndexNode;
import libbun.ast.literal.BunArrayLiteralNode;
import libbun.ast.literal.BunMapEntryNode;
import libbun.ast.literal.BunMapLiteralNode;
import libbun.ast.literal.BunNullNode;
import libbun.ast.statement.BunReturnNode;
import libbun.ast.statement.BunThrowNode;
import libbun.ast.statement.BunTryNode;
import libbun.parser.BLogger;
import libbun.type.BClassType;
import libbun.type.BFuncType;
import libbun.type.BType;
import libbun.util.BArray;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.BMap;
import libbun.util.Var;
import libbun.util.ZenMethod;

public class CSharpGenerator extends OldSourceGenerator {

	@BField private BunFunctionNode MainFuncNode = null;
	@BField private final BArray<BunFunctionNode> ExportFunctionList = new BArray<BunFunctionNode>(new BunFunctionNode[4]);

	public CSharpGenerator() {
		super("cs", "5.0");
		this.IntLiteralSuffix="";
		this.TopType = "object";
		this.SetNativeType(BType.BooleanType, "bool");
		this.SetNativeType(BType.IntType, "long");  // for beautiful code
		this.SetNativeType(BType.FloatType, "double");
		this.SetNativeType(BType.StringType, "string");
		this.SetNativeType(BType.VarType, "object");  // for safety

		this.SetReservedName("this", "@this");
		this.CurrentBuilder.AppendNewLine("/* end of header */", this.LineFeed);
		this.CurrentBuilder.OpenIndent("namespace ZenGenerated {");
	}

	@Override protected void GenerateImportLibrary(String LibName) {
		this.HeaderBuilder.AppendNewLine("using ", LibName, this.SemiColon);
	}

	@Override @ZenMethod protected void Finish(String FileName) {
		//if(FileName == null) {
		FileName = "ZenMain";
		/*}
		else {
			@Var int loc = FileName.lastIndexOf('/');
			if(loc != -1) {
				FileName = FileName.substring(loc+1);
			}
		}*/

		this.GenerateClass("public static", FileName, BClassType._ObjectType);
		this.CurrentBuilder.OpenIndent(" {");
		if(this.MainFuncNode != null) {
			this.CurrentBuilder.AppendNewLine("public static void Main(String[] a)");
			this.CurrentBuilder.OpenIndent(" {");
			//this.CurrentBuilder.AppendNewLine(this.NameFunctionClass(this.MainFuncNode.FuncName(), ZType.VoidType, 0), ".f();");
			this.CurrentBuilder.AppendNewLine(this.MainFuncNode.FuncName() + "();");
			this.NameFunctionClass(this.MainFuncNode.FuncName(), BType.VoidType, 0);
			this.CurrentBuilder.CloseIndent("}");
		}
		this.CurrentBuilder.CloseIndent("}");
		this.CurrentBuilder.CloseIndent("}"); // end of namespace
		this.CurrentBuilder.AppendLineFeed();
	}


	@Override protected void GenerateCode(BType ContextType, BNode Node) {
		if(Node.IsUntyped() && !Node.IsErrorNode() && !(Node instanceof BunFuncNameNode)) {
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

	@Override public void VisitArrayLiteralNode(BunArrayLiteralNode Node) {
		if(Node.GetListSize() == 0) {
			this.CurrentBuilder.Append("new ", this.GetCSharpTypeName(Node.Type, false), "()");
		}
		else {
			this.ImportLibrary("System.Collections.Generic");
			this.CurrentBuilder.Append("new ", this.GetCSharpTypeName(Node.Type, false));
			this.VisitListNode("{", Node, "}");
		}
	}

	@Override public void VisitMapLiteralNode(BunMapLiteralNode Node) {
		this.CurrentBuilder.Append("new ", this.GetCSharpTypeName(Node.Type, false));
		if(Node.GetListSize() > 0) {
			@Var int i = 0;
			this.CurrentBuilder.OpenIndent(" {");
			while(i < Node.GetListSize()) {
				@Var BunMapEntryNode Entry = Node.GetMapEntryNode(i);
				this.CurrentBuilder.AppendNewLine("{");
				this.GenerateCode2("", Entry.KeyNode(), this.Camma, Entry.ValueNode(), "},");
				i = i + 1;
			}
			this.CurrentBuilder.CloseIndent("}");
		}else{
			this.CurrentBuilder.Append("()");
		}
	}

	@Override public void VisitNewObjectNode(NewObjectNode Node) {
		this.CurrentBuilder.Append("new " + this.NameClass(Node.Type));
		this.VisitListNode("(", Node, ")");
	}

	@Override public void VisitGetIndexNode(GetIndexNode Node) {
		this.GenerateCode(null, Node.RecvNode());
		if(Node.RecvNode().Type == BType.StringType){
			this.GenerateCode2(".Substring(", null, Node.IndexNode(), ", 1)");
		}else{
			this.GenerateCode2("[", null, Node.IndexNode(), "]");
		}
	}

	@Override public void VisitSetIndexNode(SetIndexNode Node) {
		this.GenerateCode(null, Node.RecvNode());
		this.GenerateCode2("[", null, Node.IndexNode(), "] = ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitMethodCallNode(MethodCallNode Node) {
		this.GenerateSurroundCode(Node.RecvNode());
		this.CurrentBuilder.Append(".");
		this.CurrentBuilder.Append(Node.MethodName());
		this.VisitListNode("(", Node, ")");
	}

	//	@Override public void VisitFuncCallNode(ZFuncCallNode Node) {
	//
	//		if(Node.IsStaticFuncCall()) {
	//			this.GenerateCode(null, Node.FunctionNode());
	//			this.VisitListNode("(", Node, ")");
	//		}
	//		else {
	//			this.GenerateCode(null, Node.FunctionNode());
	//			this.VisitListNode("(", Node, ")");
	//		}
	//	}

	//	@Override public void VisitCastNode(ZCastNode Node) {
	//		this.CurrentBuilder.Append("(");
	//		this.VisitType(Node.Type);
	//		this.CurrentBuilder.Append(")");
	//		this.GenerateSurroundCode(Node.ExprNode());
	//	}

	@Override public void VisitThrowNode(BunThrowNode Node) {
		this.CurrentBuilder.Append("throw ");
		this.GenerateCode2("new Exception((", null, Node.ExprNode(),").ToString())");
	}

	@Override public void VisitTryNode(BunTryNode Node) {
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

	private String GetCSharpTypeName(BType Type, boolean Boxing) {
		this.ImportLibrary("System");
		this.ImportLibrary("System.Diagnostics");
		this.ImportLibrary("System.Collections.Generic");
		this.ImportLibrary("System.Linq");
		this.ImportLibrary("System.Text");
		if(Type.IsArrayType()) {
			this.ImportLibrary("System.Collections.Generic");
			return "List<" + this.GetCSharpTypeName(Type.GetParamType(0), true) + ">";
		}
		if(Type.IsMapType()) {
			this.ImportLibrary("System.Collections.Generic");
			return "Dictionary<string," + this.GetCSharpTypeName(Type.GetParamType(0), true) + ">";
		}
		if(Type instanceof BFuncType) {
			return this.GetFuncTypeClass((BFuncType)Type);
		}
		if(Type instanceof BClassType) {
			return this.NameClass(Type);
		}
		if(Boxing) {
			if(Type.IsIntType()) {
				return "Int64";
			}
			if(Type.IsFloatType()) {
				return "Double";
			}
			if(Type.IsBooleanType()) {
				return "Bool";
			}
		}
		return this.GetNativeTypeName(Type);
	}


	@BField private final BMap<String> FuncNameMap = new BMap<String>(null);

	String GetFuncTypeClass(BFuncType FuncType) {
		@Var String ClassName = this.FuncNameMap.GetOrNull(FuncType.GetUniqueName());
		if(ClassName == null){
			@Var SourceBuilder MainBuilder = this.CurrentBuilder;
			this.CurrentBuilder = new SourceBuilder(this, null);
			@Var boolean HasReturnValue = !FuncType.GetReturnType().equals(BType.VoidType);
			if(HasReturnValue){
				this.CurrentBuilder.Append("Func");
			}else{
				this.CurrentBuilder.Append("Action");
			}
			this.CurrentBuilder.Append("<");
			@Var int i = 0;
			while(i < FuncType.GetFuncParamSize()) {
				if(i > 0) {
					this.CurrentBuilder.Append(this.Camma);
				}
				this.GenerateTypeName(FuncType.GetFuncParamType(i));
				i = i + 1;
			}
			if(HasReturnValue){
				this.CurrentBuilder.Append(this.Camma);
				this.GenerateTypeName(FuncType.GetReturnType());
			}
			this.CurrentBuilder.Append(">");
			ClassName = this.CurrentBuilder.toString();
			this.CurrentBuilder = MainBuilder;
			this.FuncNameMap.put(FuncType.GetUniqueName(), ClassName);
		}
		return ClassName;
	}

	@Override protected void GenerateTypeName(BType Type) {
		if(Type instanceof BFuncType) {
			this.CurrentBuilder.Append(this.GetFuncTypeClass((BFuncType)Type));
		}
		else {
			this.CurrentBuilder.Append(this.GetCSharpTypeName(Type.GetRealType(), false));
		}
	}

	@Override
	protected void VisitVarDeclNode(BunLetVarNode Node) {
		if(Node.InitValueNode() instanceof BunNullNode){
			this.GenerateTypeName(Node.DeclType());
			this.CurrentBuilder.Append(" ");
		}else if(Node.InitValueNode() instanceof BunFunctionNode){
			this.GenerateTypeName(Node.DeclType());
			this.CurrentBuilder.Append(" ");
		}else{
			this.CurrentBuilder.Append("var ");
		}
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
		this.CurrentBuilder.Append(" = ");
		this.GenerateCode(null, Node.InitValueNode());
		this.CurrentBuilder.Append(this.SemiColon);
		if(Node.HasNextVarNode()) { this.VisitVarDeclNode(Node.NextVarNode()); }
	}

	@Override public void VisitLetNode(BunLetVarNode Node) {
		this.CurrentBuilder.AppendNewLine("public static partial class ZenMain");
		this.CurrentBuilder.OpenIndent(" {");
		this.GenerateClassField("static readonly ", Node.GetAstType(BunLetVarNode._InitValue), Node.GetUniqueName(this), null);
		this.GenerateCode2(" = ", null, Node.InitValueNode(), this.SemiColon);
		this.CurrentBuilder.CloseIndent("}");
	}

	@Override protected void VisitParamNode(BunLetVarNode Node) {
		this.GenerateTypeName(Node.Type);
		this.CurrentBuilder.Append(" ");
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetGivenName()));
	}

	@Override public void VisitFunctionNode(BunFunctionNode Node) {
		@Var boolean IsLambda = (Node.FuncName() == null);
		if(IsLambda){
			this.GenerateLambdaFunction(Node);
			return;
		}
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
		}
	}

	private void GenerateLambdaFunction(BunFunctionNode Node){
		this.VisitListNode("(", Node, ") => ");
		if(Node.BlockNode().GetListSize() == 1){
			@Var BNode FirstNode = Node.BlockNode().GetListAt(0);
			if(FirstNode instanceof BunReturnNode){
				this.GenerateCode(null, ((BunReturnNode)FirstNode).ExprNode());
				return;
			}
		}
		this.GenerateCode(null, Node.BlockNode());
	}

	private void VisitInstanceMethodParameters(BunFunctionNode VargNode){
		this.CurrentBuilder.Append("(");
		@Var int i = 1;
		while(i < VargNode.GetListSize()) {
			@Var BNode ParamNode = VargNode.GetListAt(i);
			if (i > 0) {
				this.CurrentBuilder.Append(this.Camma);
			}
			this.GenerateCode(null, ParamNode);
			i = i + 1;
		}
		this.CurrentBuilder.Append(")");
	}

	private String GenerateFunctionAsClass(String FuncName, BunFunctionNode Node) {
		@Var BunLetVarNode FirstParam = Node.GetListSize() == 0 ? null : (BunLetVarNode)Node.GetListAt(0);
		@Var boolean IsInstanceMethod = FirstParam != null && FirstParam.GetGivenName().equals("this");

		this.GenerateClass("public static", "ZenMain", Node.GetFuncType());
		this.CurrentBuilder.OpenIndent(" { ");
		this.CurrentBuilder.AppendNewLine("public static ");
		this.GenerateTypeName(Node.ReturnType());
		this.CurrentBuilder.Append(" ");
		this.CurrentBuilder.Append(FuncName);
		if(IsInstanceMethod){
			this.VisitFuncParamNode("(this ", Node, ")");
		}else{
			this.VisitFuncParamNode("(", Node, ")");
		}

		this.GenerateCode(null, Node.BlockNode());

		this.CurrentBuilder.CloseIndent("}");
		this.CurrentBuilder.AppendNewLine();
		return FuncName;
	}

	@Override public void VisitInstanceOfNode(BInstanceOfNode Node) {
		this.GenerateCode(null, Node.AST[BInstanceOfNode._Left]);
		this.CurrentBuilder.Append(" is ");
		this.GenerateTypeName(Node.TargetType());
	}


	private void GenerateClass(String Qualifier, String ClassName, BType SuperType) {
		if(Qualifier != null && Qualifier.length() > 0) {
			this.CurrentBuilder.AppendNewLine(Qualifier);
			this.CurrentBuilder.AppendWhiteSpace("partial class ", ClassName);
		}
		else {
			this.CurrentBuilder.AppendNewLine("partial class ", ClassName);
		}
		if(!SuperType.Equals(BClassType._ObjectType) && !SuperType.IsFuncType()) {
			this.CurrentBuilder.Append(" : ");
			this.GenerateTypeName(SuperType);
		}
	}

	private void GenerateClassField(String Qualifier, BType FieldType, String FieldName, String Value) {
		if(Qualifier.length() > 1){
			this.CurrentBuilder.AppendNewLine(Qualifier);
			this.CurrentBuilder.Append("public ");
		}else{
			this.CurrentBuilder.AppendNewLine("public ");
		}
		this.GenerateTypeName(FieldType);
		this.CurrentBuilder.Append(" ", FieldName);
		if(Value != null) {
			this.CurrentBuilder.Append(" = ", Value);
			this.CurrentBuilder.Append(this.SemiColon);
		}
	}

	@Override public void VisitClassNode(BunClassNode Node) {
		@Var BType SuperType = Node.ClassType.GetSuperType();
		@Var String ClassName = this.NameClass(Node.ClassType);
		this.GenerateClass("public", ClassName, SuperType);
		this.CurrentBuilder.OpenIndent(" {");
		@Var int i = 0;
		while (i < Node.GetListSize()) {
			@Var BunLetVarNode FieldNode = Node.GetFieldNode(i);
			this.GenerateClassField("", FieldNode.DeclType(), FieldNode.GetGivenName(), null);
			this.CurrentBuilder.Append(this.SemiColon);
			i = i + 1;
		}
		this.CurrentBuilder.AppendNewLine();

		i = 0;
		//while(i < Node.ClassType.GetFieldSize()) {
		//@Var ZClassField Field = Node.ClassType.GetFieldAt(i);
		//			if(Field.FieldType.IsFuncType()) {
		//				this.GenerateClassField("static", Field.FieldType, this.NameMethod(Node.ClassType, Field.FieldName), "null");
		//				this.CurrentBuilder.Append(this.SemiColon);
		//			}
		//i = i + 1;
		//}

		this.CurrentBuilder.AppendNewLine("public ", this.NameClass(Node.ClassType), "()");
		this.CurrentBuilder.OpenIndent(" {");
		//this.CurrentBuilder.AppendNewLine("super();");
		while (i < Node.GetListSize()) {
			@Var BunLetVarNode FieldNode = Node.GetFieldNode(i);
			this.CurrentBuilder.AppendNewLine("this.", FieldNode.GetGivenName(), "=");
			this.GenerateCode(null, FieldNode.InitValueNode());
			this.CurrentBuilder.Append(this.SemiColon);
			i = i + 1;
		}
		i = 0;
		while(i < Node.ClassType.GetFieldSize()) {
			//@Var ZClassField Field = Node.ClassType.GetFieldAt(i);
			//			if(Field.FieldType.IsFuncType()) {
			//				this.CurrentBuilder.AppendNewLine("if(", this.NameMethod(Node.ClassType, Field.FieldName), " != null) ");
			//				this.CurrentBuilder.OpenIndent("{");
			//				this.CurrentBuilder.AppendNewLine("this.", Field.FieldName, "=");
			//				this.CurrentBuilder.Append(this.NameMethod(Node.ClassType, Field.FieldName), ";", this.LineFeed);
			//				this.CurrentBuilder.CloseIndent("}");
			//			}
			i = i + 1;
		}

		this.CurrentBuilder.CloseIndent("}"); /* end of constructor*/
		this.CurrentBuilder.CloseIndent("}"); /* end of class */
		this.CurrentBuilder.AppendLineFeed();
	}

	@Override public void VisitErrorNode(ErrorNode Node) {
		BLogger._LogError(Node.SourceToken, Node.ErrorMessage);
		this.CurrentBuilder.Append("ThrowError(");
		this.CurrentBuilder.Append(BLib._QuoteString(Node.ErrorMessage));
		this.CurrentBuilder.Append(")");
	}

	//	@Override public void VisitExtendedNode(ZNode Node) {
	//
	//	}


}
