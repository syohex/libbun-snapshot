package libbun.encode;

import libbun.ast.AbstractListNode;
import libbun.ast.BNode;
import libbun.ast.DesugarNode;
import libbun.ast.LocalDefinedNode;
import libbun.ast.SyntaxSugarNode;
import libbun.ast.decl.BunFunctionNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.ast.decl.TopLevelNode;
import libbun.ast.expression.BunMacroNode;
import libbun.ast.literal.BunAsmNode;
import libbun.ast.literal.LiteralNode;
import libbun.ast.unary.BunCastNode;
import libbun.parser.BLangInfo;
import libbun.type.BType;
import libbun.util.BArray;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.Nullable;
import libbun.util.Var;
import libbun.util.ZenMethod;

public abstract class SourceGenerator extends AbstractGenerator {
	@BField private final BArray<SourceBuilder> BuilderList = new BArray<SourceBuilder>(new SourceBuilder[4]);
	@BField protected SourceBuilder Header;
	@BField protected SourceBuilder Source;
	@BField protected String LineFeed = "\n";
	@BField protected String Tab = "   ";
	@BField protected boolean HasMainFunction = false;

	public SourceGenerator(BLangInfo LangInfo) {
		super(LangInfo);
		this.InitBuilderList();
	}

	@ZenMethod protected void InitBuilderList() {
		this.BuilderList.clear(0);
		this.Header = this.AppendNewSourceBuilder();
		this.Source = this.AppendNewSourceBuilder();
	}

	protected final SourceBuilder AppendNewSourceBuilder() {
		@Var SourceBuilder Builder = new SourceBuilder(this, this.Source);
		this.BuilderList.add(Builder);
		return Builder;
	}

	protected final SourceBuilder InsertNewSourceBuilder() {
		@Var SourceBuilder Builder = new SourceBuilder(this, this.Source);
		@Var int i = 0;
		while(i < this.BuilderList.size()) {
			if(this.BuilderList.ArrayValues[i] == this.Source) {
				this.BuilderList.add(i, Builder);
				return Builder;
			}
			i = i + 1;
		}
		this.BuilderList.add(Builder);
		return Builder;
	}

	protected abstract void GenerateImportLibrary(String LibName);

	protected final void LoadInlineLibrary(String FileName, String Delim) {
		@Var String Path = this.LangInfo.GetLibPath2(FileName);
		BLib._LoadInlineLibrary(Path, this.SymbolMap, Delim);
	}

	public final void ImportLibrary(@Nullable String LibNames) {
		if(LibNames != null) {
			@Var String LibName = LibNames;
			@Var int loc = LibNames.indexOf(";");
			if(loc > 0) {
				LibName = LibNames.substring(0, loc);
			}
			@Var String Imported = this.ImportedLibraryMap.GetOrNull(LibName);
			if(Imported == null) {
				if(LibName.startsWith("@")) {
					this.ImportLibrary(this.SymbolMap.GetValue(LibName+";", null));
					this.Header.AppendNewLine();
					this.Header.AppendCode(this.SymbolMap.GetValue(LibName, LibName));
				}
				else {
					this.GenerateImportLibrary(LibName);
				}
				this.ImportedLibraryMap.put(LibName, LibName);
			}
			if(loc > 0) {
				this.ImportLibrary(LibNames.substring(loc+1));
			}
		}
	}

	@ZenMethod protected void Finish(String FileName) {
	}

	@Override public final void WriteTo(@Nullable String FileName) {
		this.Finish(FileName);
		if(this.HasMainFunction) {
			this.Source.AppendNewLine(this.SymbolMap.GetValue("@main", "@main"));
		}
		this.Logger.OutputErrorsToStdErr();
		BLib._WriteTo(this.LangInfo.NameOutputFile(FileName), this.BuilderList);
		this.InitBuilderList();
	}

	@Override public final String GetSourceText() {
		this.Finish(null);
		@Var SourceBuilder sb = new SourceBuilder(this, null);
		@Var int i = 0;
		while(i < this.BuilderList.size()) {
			@Var SourceBuilder Builder = this.BuilderList.ArrayValues[i];
			sb.Append(Builder.toString());
			Builder.Clear();
			sb.Append(this.LineFeed);
			i = i + 1;
		}
		this.InitBuilderList();
		return BLib._SourceBuilderToString(sb);
	}

	@Override public void Perform() {
		this.Logger.OutputErrorsToStdErr();
		BLib._PrintLine("---");
		BLib._PrintLine(this.GetSourceText());
		this.InitBuilderList();
	}



	// Generator
	protected void GenerateTypeName(BType Type) {
		this.Source.Append(this.GetNativeTypeName(Type.GetRealType()));
	}

	protected final void GenerateExpression(String Pre, BNode Node, String Post) {
		if(Pre != null && Pre.length() > 0) {
			this.Source.Append(Pre);
		}
		this.GenerateExpression(Node);
		if(Post != null && Post.length() > 0) {
			this.Source.Append(Post);
		}
	}

	protected final void GenerateExpression(String Pre, BNode Node, String Delim, BNode Node2, String Post) {
		if(Pre != null && Pre.length() > 0) {
			this.Source.Append(Pre);
		}
		this.GenerateExpression(Node);
		if(Delim != null && Delim.length() > 0) {
			this.Source.Append(Delim);
		}
		this.GenerateExpression(Node2);
		if(Post != null && Post.length() > 0) {
			this.Source.Append(Post);
		}
	}

	protected void GenerateStatement(BNode Node, @Nullable String SemiColon) {
		this.Source.AppendNewLine();
		if(Node instanceof BunCastNode && Node.Type == BType.VoidType) {
			Node.AST[BunCastNode._Expr].Accept(this);
		}
		else {
			Node.Accept(this);
		}
		if(SemiColon != null && (!this.Source.EndsWith('}') || !this.Source.EndsWith(';'))) {
			this.Source.Append(SemiColon);
		}
	}

	protected abstract void VisitParamNode(BunLetVarNode Node);

	protected final void GenerateParamNode(String OpenToken, BunFunctionNode VargNode, String Comma, String CloseToken) {
		this.Source.Append(OpenToken);
		@Var int i = 0;
		while(i < VargNode.GetListSize()) {
			@Var BunLetVarNode ParamNode = VargNode.GetParamNode(i);
			if (i > 0) {
				this.Source.Append(Comma);
			}
			this.VisitParamNode(ParamNode);
			i = i + 1;
		}
		this.Source.Append(CloseToken);
	}

	@Override public final void VisitAsmNode(BunAsmNode Node) {
		this.ImportLibrary(Node.RequiredLibrary);
		this.Source.Append(Node.GetMacroText());
	}

	@Override public void VisitMacroNode(BunMacroNode Node) {
		@Var String Macro = Node.GetMacroText();
		//		@Var BFuncType FuncType = Node.GetFuncType();
		@Var int fromIndex = 0;
		@Var int BeginNum = Macro.indexOf("$[", fromIndex);
		while(BeginNum != -1) {
			@Var int EndNum = Macro.indexOf("]", BeginNum + 2);
			if(EndNum == -1) {
				break;
			}
			this.Source.Append(Macro.substring(fromIndex, BeginNum));
			@Var int Index = (int)BLib._ParseInt(Macro.substring(BeginNum+2, EndNum));
			if(Node.AST[Index] != null) {
				//this.GenerateCode(FuncType.GetFuncParamType(Index), Node.AST[Index]);
				this.GenerateExpression(Node.AST[Index]);
			}
			fromIndex = EndNum + 1;
			BeginNum = Macro.indexOf("$[", fromIndex);
		}
		this.Source.Append(Macro.substring(fromIndex));
		if(Node.MacroFunc.RequiredLibrary != null) {
			this.ImportLibrary(Node.MacroFunc.RequiredLibrary);
		}
	}

	@Override public void VisitSyntaxSugarNode(SyntaxSugarNode Node) {
		@Var DesugarNode DeNode = Node.DeSugar(this, this.TypeChecker);
		@Var int i = 0;
		while(i < DeNode.GetAstSize()) {
			this.GenerateStatement(DeNode.AST[i], ";");
			i = i + 1;
		}
	}

	@Override public final void VisitLiteralNode(LiteralNode Node) {
	}


	@Override public final void VisitTopLevelNode(TopLevelNode Node) {
	}

	@Override public void VisitLocalDefinedNode(LocalDefinedNode Node) {
	}


	// Generator

	protected void GenerateListNode(String OpenToken, AbstractListNode VargNode, String CommaToken, String CloseToken) {
		this.Source.Append(OpenToken);
		@Var int i = 0;
		while(i < VargNode.GetListSize()) {
			@Var BNode ParamNode = VargNode.GetListAt(i);
			if (i > 0) {
				this.Source.Append(CommaToken);
			}
			this.GenerateExpression(ParamNode);
			i = i + 1;
		}
		this.Source.Append(CloseToken);
	}



}
