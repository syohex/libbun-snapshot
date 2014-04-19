package libbun.parser;

import libbun.util.BField;
import libbun.util.BMatchFunction;
import libbun.util.BTokenFunction;
import libbun.util.BunMap;
import libbun.util.LibBunSystem;
import libbun.util.Var;

public final class LibBunParser {
	@BField LibBunParser StackedParser;
	@BField LibBunTokenFuncChain[]         TokenMatrix = null;
	@BField BunMap<LibBunSyntax>           SyntaxTable = null;

	public LibBunParser(LibBunParser StackedParser) {
		this.StackedParser = StackedParser;
		this.TokenMatrix = LibBunSystem._NewTokenMatrix();
		this.SyntaxTable = new BunMap<LibBunSyntax>(null);
	}

	public LibBunParser Pop() {
		return this.StackedParser;
	}

	// TokenMatrix
	public final LibBunTokenFuncChain GetTokenFunc(int ZenChar) {
		return this.TokenMatrix[ZenChar];
	}

	private final LibBunTokenFuncChain JoinParentFunc(BTokenFunction Func, LibBunTokenFuncChain Parent) {
		if(Parent != null && Parent.Func == Func) {
			return Parent;
		}
		return new LibBunTokenFuncChain(Func, Parent);
	}

	public final void AppendTokenFunc(String keys, BTokenFunction TokenFunc) {
		@Var int i = 0;
		while(i < keys.length()) {
			@Var int kchar = LibBunSystem._GetTokenMatrixIndex(LibBunSystem._GetChar(keys, i));
			this.TokenMatrix[kchar] = this.JoinParentFunc(TokenFunc, this.TokenMatrix[kchar]);
			i = i + 1;
		}
	}

	public final void SetSyntaxPattern(String PatternName, LibBunSyntax Syntax) {
		if(this.SyntaxTable == null) {
			this.SyntaxTable = new BunMap<LibBunSyntax>(null);
		}
		this.SyntaxTable.put(PatternName, Syntax);
	}

	public final LibBunSyntax GetSyntaxPattern(String PatternName) {
		return this.SyntaxTable.GetValue(PatternName, null);
	}

	public final static String _RightPatternSymbol(String PatternName) {
		return "\t" + PatternName;
	}

	public final LibBunSyntax GetRightSyntaxPattern(String PatternName) {
		return this.GetSyntaxPattern(LibBunParser._RightPatternSymbol(PatternName));
	}

	private void AppendSyntaxPattern(String PatternName, LibBunSyntax NewPattern) {
		LibBunSystem._Assert(NewPattern.ParentPattern == null);
		@Var LibBunSyntax ParentPattern = this.GetSyntaxPattern(PatternName);
		NewPattern.ParentPattern = ParentPattern;
		this.SetSyntaxPattern(PatternName, NewPattern);
	}

	public final void DefineStatement(String PatternName, BMatchFunction MatchFunc) {
		@Var int Alias = PatternName.indexOf(" ");
		@Var String Name = PatternName;
		if(Alias != -1) {
			Name = PatternName.substring(0, Alias);
		}
		@Var LibBunSyntax Pattern = new LibBunSyntax(Name, MatchFunc);
		Pattern.IsStatement = true;
		this.AppendSyntaxPattern(Name, Pattern);
		if(Alias != -1) {
			this.DefineStatement(PatternName.substring(Alias+1), MatchFunc);
		}
	}

	public final void DefineExpression(String PatternName, BMatchFunction MatchFunc) {
		@Var int Alias = PatternName.indexOf(" ");
		@Var String Name = PatternName;
		if(Alias != -1) {
			Name = PatternName.substring(0, Alias);
		}
		@Var LibBunSyntax Pattern = new LibBunSyntax(Name, MatchFunc);
		this.AppendSyntaxPattern(Name, Pattern);
		if(Alias != -1) {
			this.DefineExpression(PatternName.substring(Alias+1), MatchFunc);
		}
	}

	public final void DefineRightExpression(String PatternName, BMatchFunction MatchFunc) {
		@Var int Alias = PatternName.indexOf(" ");
		@Var String Name = PatternName;
		if(Alias != -1) {
			Name = PatternName.substring(0, Alias);
		}
		@Var LibBunSyntax Pattern = new LibBunSyntax(Name, MatchFunc);
		this.AppendSyntaxPattern(LibBunParser._RightPatternSymbol(Name), Pattern);
		if(Alias != -1) {
			this.DefineRightExpression(PatternName.substring(Alias+1), MatchFunc);
		}
	}

}
