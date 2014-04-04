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


package libbun.parser;

import libbun.ast.BBlockNode;
import libbun.ast.BListNode;
import libbun.ast.BNode;
import libbun.ast.ZDesugarNode;
import libbun.ast.ZSugarNode;
import libbun.ast.decl.BClassNode;
import libbun.ast.decl.BFunctionNode;
import libbun.ast.decl.BLetVarNode;
import libbun.ast.decl.ZTopLevelNode;
import libbun.ast.error.BErrorNode;
import libbun.ast.literal.BDefaultValueNode;
import libbun.ast.literal.BNullNode;
import libbun.type.BClassType;
import libbun.type.BFunc;
import libbun.type.BFuncType;
import libbun.type.BMacroFunc;
import libbun.type.BPrototype;
import libbun.type.BType;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.Nullable;
import libbun.util.Var;
import libbun.util.BMap;
import libbun.util.BIgnored;
import libbun.util.ZenMethod;

public abstract class BGenerator extends BVisitor {
	@BField public BMap<String>        ImportedLibraryMap = new BMap<String>(null);
	@BField private final BMap<BFunc>  DefinedFuncMap = new BMap<BFunc>(null);

	@BField public final BNameSpace      RootNameSpace;
	@BField public BLogger               Logger;
	@BField public BTypeChecker          TypeChecker;
	@BField public BLangInfo             LangInfo;
	@BField protected String             TopLevelSymbol = null;
	@BField private int                  UniqueNumber = 0;
	@BField private boolean              StoppedVisitor;

	protected BGenerator(BLangInfo LangInfo) {
		this.RootNameSpace = new BNameSpace(this, null);
		this.Logger        = new BLogger();
		this.LangInfo      = LangInfo;
		this.TypeChecker   = null;
		this.StoppedVisitor = false;
	}

	public final void SetTypeChecker(BTypeChecker TypeChecker) {
		this.TypeChecker = TypeChecker;
	}

	@Override public final void EnableVisitor() {
		this.StoppedVisitor = false;
	}

	@Override public final void StopVisitor() {
		this.StoppedVisitor = true;
	}

	@Override public final boolean IsVisitable() {
		return !this.StoppedVisitor;
	}

	@ZenMethod protected void GenerateImportLibrary(String LibName) {
		//	this.HeaderBuilder.AppendNewLine("require ", LibName, this.LineFeed);
	}

	public final void ImportLibrary(@Nullable String LibName) {
		if(LibName != null) {
			@Var String Imported = this.ImportedLibraryMap.GetOrNull(LibName);
			if(Imported == null) {
				this.GenerateImportLibrary(LibName);
				this.ImportedLibraryMap.put(LibName, LibName);
			}
		}
	}

	public final void SetDefinedFunc(BFunc Func) {

		this.DefinedFuncMap.put(Func.GetSignature(), Func);
	}

	private String NameConverterFunc(BType FromType, BType ToType) {
		return FromType.GetUniqueName() + "T" + ToType.GetUniqueName();
	}

	public final void SetConverterFunc(BType FromType, BType ToType, BFunc Func) {
		//System.out.println("set " + this.NameConverterFunc(FromType, ToType));
		this.DefinedFuncMap.put(this.NameConverterFunc(FromType, ToType), Func);
	}

	public final BFunc LookupConverterFunc(BType FromType, BType ToType) {
		while(FromType != null) {
			@Var BFunc Func = this.DefinedFuncMap.GetOrNull(this.NameConverterFunc(FromType, ToType));
			//System.out.println("get " + this.NameConverterFunc(FromType, ToType) + ", func="+ Func);
			if(Func != null) {
				return Func;
			}
			FromType = FromType.GetSuperType();
		}
		return null;
	}

	@ZenMethod public void WriteTo(@Nullable String FileName) {
		// TODO Stub
	}

	@ZenMethod public String GetSourceText() {
		return null;
	}

	@ZenMethod public BType GetFieldType(BType BaseType, String Name) {
		return BType.VarType;     // undefined
	}

	@ZenMethod public BType GetSetterType(BType BaseType, String Name) {
		return BType.VarType;     // undefined
	}

	@ZenMethod public BFuncType GetConstructorFuncType(BType ClassType, BListNode List) {
		//return null;              // undefined and undefined error
		return BFuncType._FuncType;    // undefined and no error
	}

	@ZenMethod public BFuncType GetMethodFuncType(BType RecvType, String MethodName, BListNode List) {
		//return null;              // undefined and undefined error
		return BFuncType._FuncType;     // undefined and no error
	}

	// Naming

	public final int GetUniqueNumber() {
		@Var int UniqueNumber = this.UniqueNumber;
		this.UniqueNumber = this.UniqueNumber + 1;
		return UniqueNumber;
	}

	public final String NameUniqueSymbol(String Symbol) {
		return Symbol + "Z" + this.GetUniqueNumber();
	}

	public final String NameUniqueSymbol(String Symbol, int NameIndex) {
		return Symbol + "__B" + this.GetUniqueNumber();
	}

	public String NameGlobalNameClass(String Name) {
		return "G__" + Name;
	}

	public final String NameClass(BType ClassType) {
		return ClassType.ShortName + "" + ClassType.TypeId;
	}

	public final String NameFunctionClass(String FuncName, BFuncType FuncType) {
		return "F__" + FuncType.StringfySignature(FuncName);
	}

	public final String NameFunctionClass(String FuncName, BType RecvType, int FuncParamSize) {
		return "F__" + BFunc._StringfySignature(FuncName, FuncParamSize, RecvType);
	}

	public final String NameType(BType Type) {
		if(Type.IsArrayType()) {
			return "ArrayOf" + this.NameType(Type.GetParamType(0)) + "_";
		}
		if(Type.IsMapType()) {
			return "MapOf" + this.NameType(Type.GetParamType(0)) + "_";
		}
		if(Type instanceof BFuncType) {
			@Var String s = "FuncOf";
			@Var int i = 0;
			while(i < Type.GetParamSize()) {
				s = s +  this.NameType(Type.GetParamType(i));
				i = i + 1;
			}
			return s + "_";
		}
		if(Type instanceof BClassType) {
			return this.NameClass(Type);
		}
		return Type.GetName();
	}

	//

	public final BPrototype SetPrototype(BNode Node, String FuncName, BFuncType FuncType) {
		@Var BFunc Func = this.GetDefinedFunc(FuncName, FuncType);
		if(Func != null) {
			if(!FuncType.Equals(Func.GetFuncType())) {
				BLogger._LogError(Node.SourceToken, "function has been defined diffrently: " + Func.GetFuncType());
				return null;
			}
			if(Func instanceof BPrototype) {
				return (BPrototype)Func;
			}
			BLogger._LogError(Node.SourceToken, "function has been defined as macro" + Func);
			return null;
		}
		@Var BPrototype	Proto= new BPrototype(0, FuncName, FuncType, Node.SourceToken);
		this.DefinedFuncMap.put(Proto.GetSignature(), Proto);
		return Proto;
	}

	public final BFunc GetDefinedFunc(String GlobalName) {
		@Var BFunc Func = this.DefinedFuncMap.GetOrNull(GlobalName);
		if(Func == null && BLib._IsLetter(BLib._GetChar(GlobalName, 0))) {
			//			System.out.println("AnotherName = " + GlobalName + ", " + LibZen._AnotherName(GlobalName));
			Func = this.DefinedFuncMap.GetOrNull(BLib._AnotherName(GlobalName));
		}
		//System.out.println("sinature="+GlobalName+", func="+Func);
		return Func;
	}

	public final BFunc GetDefinedFunc(String FuncName, BFuncType FuncType) {
		return this.GetDefinedFunc(FuncType.StringfySignature(FuncName));
	}

	public final BFunc GetDefinedFunc(String FuncName, BType RecvType, int FuncParamSize) {
		return this.GetDefinedFunc(BFunc._StringfySignature(FuncName, FuncParamSize, RecvType));
	}

	public final BFunc LookupFunc(String FuncName, BType RecvType, int FuncParamSize) {
		@Var BFunc Func = this.GetDefinedFunc(BFunc._StringfySignature(FuncName, FuncParamSize, RecvType));
		while(Func == null) {
			RecvType = RecvType.GetSuperType();
			if(RecvType == null) {
				break;
			}
			Func = this.GetDefinedFunc(BFunc._StringfySignature(FuncName, FuncParamSize, RecvType));
			//			if(RecvType.IsVarType()) {
			//				break;
			//			}
		}
		return Func;
	}

	public final BMacroFunc GetMacroFunc(String FuncName, BType RecvType, int FuncParamSize) {
		@Var BFunc Func = this.GetDefinedFunc(BFunc._StringfySignature(FuncName, FuncParamSize, RecvType));
		if(Func instanceof BMacroFunc) {
			return ((BMacroFunc)Func);
		}
		return null;
	}

	public final void VisitUndefinedNode(BNode Node) {
		@Var BErrorNode ErrorNode = new BErrorNode(Node.ParentNode, Node.SourceToken, "undefined node:" + Node.toString());
		this.VisitErrorNode(ErrorNode);
	}

	@Override public final void VisitDefaultValueNode(BDefaultValueNode Node) {
		this.VisitNullNode(new BNullNode(Node.ParentNode, null));
	}

	@Override public void VisitSugarNode(ZSugarNode Node) {
		@Var ZDesugarNode DeNode = Node.DeSugar(this, this.TypeChecker);
		@Var int i = 0;
		while(i < DeNode.GetAstSize()) {
			DeNode.AST[i].Accept(this);  // FIXME
			i = i + 1;
		}
	}

	@ZenMethod protected void GenerateCode(BType ContextType, BNode Node) {
		Node.Accept(this);
	}

	@ZenMethod public void GenerateStatement(BNode Node) {
		Node.Accept(this);
	}

	protected boolean ExecStatement(BNode Node, boolean IsInteractive) {
		//this.InteractiveContext = IsInteractive;
		this.EnableVisitor();
		this.TopLevelSymbol = null;
		if(Node instanceof ZTopLevelNode) {
			((ZTopLevelNode)Node).Perform(this.RootNameSpace);
		}
		else {
			if(this.TypeChecker != null) {
				Node = this.TypeChecker.CheckType(Node, BType.VarType);
			}
			if(this.IsVisitable()) {
				if(Node instanceof BFunctionNode || Node instanceof BClassNode || Node instanceof BLetVarNode) {
					Node.Type = BType.VoidType;
					this.GenerateStatement(Node);
				}
				else {
					if(!this.LangInfo.AllowTopLevelScript) {
						@Var String FuncName = this.NameUniqueSymbol("Main");
						Node = this.TypeChecker.CreateFunctionNode(Node.ParentNode, FuncName, Node);
						this.TopLevelSymbol = FuncName;
					}
					this.GenerateStatement(Node);
				}
			}
		}
		return this.IsVisitable();
	}

	public final boolean LoadScript(String ScriptText, String FileName, int LineNumber, boolean IsInteractive) {
		@Var boolean Result = true;
		@Var BBlockNode TopBlockNode = new BBlockNode(null, this.RootNameSpace);
		@Var BTokenContext TokenContext = new BTokenContext(this, this.RootNameSpace, FileName, LineNumber, ScriptText);
		TokenContext.SkipEmptyStatement();
		@Var BToken SkipToken = TokenContext.GetToken();
		while(TokenContext.HasNext()) {
			TokenContext.SetParseFlag(BTokenContext._NotAllowSkipIndent);
			TopBlockNode.ClearListToSize(0);
			SkipToken = TokenContext.GetToken();
			@Var BNode StmtNode = TokenContext.ParsePattern(TopBlockNode, "$Statement$", BTokenContext._Required);
			if(StmtNode.IsErrorNode()) {
				TokenContext.SkipError(SkipToken);
			}
			if(!this.ExecStatement(StmtNode, IsInteractive)) {
				Result = false;
				break;
			}
			TokenContext.SkipEmptyStatement();
			TokenContext.Vacume();
		}
		this.Logger.OutputErrorsToStdErr();
		return Result;
	}


	public final boolean LoadFile(String FileName, @Nullable BToken SourceToken) {
		@Var String ScriptText = BLib._LoadTextFile(FileName);
		if(ScriptText == null) {
			BLogger._LogErrorExit(SourceToken, "file not found: " + FileName);
			return false;
		}
		return this.LoadScript(ScriptText, FileName, 1, false);
	}

	public final boolean RequireLibrary(String LibName, @Nullable BToken SourceToken) {
		@Var String Key = "_Z" + LibName.toLowerCase();
		@Var String Value = this.ImportedLibraryMap.GetOrNull(Key);
		if(Value == null) {
			@Var String Path = this.LangInfo.GetLibPath(LibName);
			@Var String Script = BLib._LoadTextFile(Path);
			if(Script == null) {
				BLogger._LogErrorExit(SourceToken, "library not found: " + LibName + " as " + Path);
				return false;
			}
			@Var boolean Result = this.LoadScript(Script, Path, 1, false);
			this.ImportedLibraryMap.put(Key, Path);
			return Result;
		}
		return true;
	}

	@BIgnored public void Perform() {
		if(this.TopLevelSymbol != null) {
			System.out.println("TODO: " + this.TopLevelSymbol);
		}
	}

	@ZenMethod public void ExecMain() {
		this.Logger.OutputErrorsToStdErr();
	}


	//	public final String Translate(String ScriptText, String FileName, int LineNumber) {
	//		@Var ZBlockNode TopBlockNode = new ZBlockNode(this.Generator.RootNameSpace);
	//		@Var ZTokenContext TokenContext = new ZTokenContext(this.Generator, this.Generator.RootNameSpace, FileName, LineNumber, ScriptText);
	//		TokenContext.SkipEmptyStatement();
	//		@Var ZToken SkipToken = TokenContext.GetToken();
	//		while(TokenContext.HasNext()) {
	//			TokenContext.SetParseFlag(ZTokenContext._NotAllowSkipIndent);
	//			TopBlockNode.ClearListAfter(0);
	//			SkipToken = TokenContext.GetToken();
	//			@Var ZNode ParsedNode = TokenContext.ParsePattern(TopBlockNode, "$Statement$", ZTokenContext._Required);
	//			if(ParsedNode.IsErrorNode()) {
	//				TokenContext.SkipError(SkipToken);
	//			}
	//			this.Exec2(ParsedNode, false);
	//			TokenContext.SkipEmptyStatement();
	//			TokenContext.Vacume();
	//		}
	//		return this.Generator.GetSourceText();
	//	}



}
