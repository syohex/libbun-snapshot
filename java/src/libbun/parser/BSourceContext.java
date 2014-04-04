package libbun.parser;

import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.Var;

public final class BSourceContext extends BSource {

	@BField int SourcePosition = 0;
	public BSourceContext(String FileName, int LineNumber, String Source, BTokenContext TokenContext) {
		super(FileName, LineNumber, Source, TokenContext);
	}

	public final int GetCharCode() {
		return BLib._GetTokenMatrixIndex(BLib._GetChar(this.SourceText, this.SourcePosition));
	}

	public final int GetPosition() {
		return this.SourcePosition;
	}

	public final boolean HasChar() {
		return this.SourceText.length() - this.SourcePosition > 0;
	}

	public final char GetCurrentChar() {
		return BLib._GetChar(this.SourceText, this.SourcePosition);
	}

	public final char GetCharAtFromCurrentPosition(int n) {
		if(this.SourcePosition+n < this.SourceText.length()) {
			return BLib._GetChar(this.SourceText, this.SourcePosition+n);
		}
		return '\0';
	}

	public final void MoveNext() {
		this.SourcePosition = this.SourcePosition + 1;
	}

	public final void SkipWhiteSpace() {
		while(this.HasChar()) {
			@Var char ch = this.GetCurrentChar();
			if(ch != ' ' && ch != '\t') {
				break;
			}
			this.MoveNext();
		}
	}

	public final void FoundIndent(int StartIndex, int EndIndex) {
		@Var BToken Token = new BIndentToken(this, StartIndex, EndIndex);
		this.SourcePosition = EndIndex;
		this.TokenContext.TokenList.add(Token);
	}

	public final void Tokenize(int StartIndex, int EndIndex) {
		this.SourcePosition = EndIndex;
		if(StartIndex < EndIndex && EndIndex <= this.SourceText.length()) {
			@Var BToken Token = new BToken(this, StartIndex, EndIndex);
			this.TokenContext.TokenList.add(Token);
		}
	}

	public final void Tokenize(String PatternName, int StartIndex, int EndIndex) {
		this.SourcePosition = EndIndex;
		if(StartIndex <= EndIndex && EndIndex <= this.SourceText.length()) {
			@Var BSyntax Pattern = this.TokenContext.NameSpace.GetSyntaxPattern(PatternName);
			if(Pattern == null) {
				@Var BToken Token = new BToken(this, StartIndex, EndIndex);
				BLogger._LogInfo(Token, "unregistered token pattern: " + PatternName);
				this.TokenContext.TokenList.add(Token);
			}
			else {
				@Var BToken Token = new BPatternToken(this, StartIndex, EndIndex, Pattern);
				this.TokenContext.TokenList.add(Token);
			}
		}
	}

	public final boolean IsDefinedSyntax(int StartIndex, int EndIndex) {
		if(EndIndex < this.SourceText.length()) {
			@Var BNameSpace NameSpace = this.TokenContext.NameSpace;
			@Var String Token = this.SourceText.substring(StartIndex, EndIndex);
			@Var BSyntax Pattern = NameSpace.GetRightSyntaxPattern(Token);
			if(Pattern != null) {
				return true;
			}
		}
		return false;
	}

	public final void TokenizeDefinedSymbol(int StartIndex) {
		//		@Var int StartIndex = this.SourcePosition;
		@Var int EndIndex = StartIndex + 2;
		while(this.IsDefinedSyntax(StartIndex, EndIndex)) {
			EndIndex = EndIndex + 1;
		}
		this.Tokenize(StartIndex, EndIndex-1);
	}

	private final void ApplyTokenFunc(BTokenFuncChain TokenFunc) {
		@Var int RollbackPosition = this.SourcePosition;
		while(TokenFunc != null) {
			this.SourcePosition = RollbackPosition;
			if(BLib._ApplyTokenFunc(TokenFunc.Func, this)) {
				return;
			}
			TokenFunc = TokenFunc.ParentFunc;
		}
		this.TokenizeDefinedSymbol(RollbackPosition);
	}

	public final boolean DoTokenize() {
		@Var int TokenSize = this.TokenContext.TokenList.size();
		@Var int CheckPosition = this.SourcePosition;
		while(this.HasChar()) {
			@Var int CharCode = this.GetCharCode();
			@Var BTokenFuncChain TokenFunc = this.TokenContext.NameSpace.GetTokenFunc(CharCode);
			this.ApplyTokenFunc(TokenFunc);
			if(this.TokenContext.TokenList.size() > TokenSize) {
				break;
			}
			if(this.SourcePosition == CheckPosition) {
				//LibZen._PrintLine("Buggy TokenFunc: " + TokenFunc);
				this.MoveNext();
			}
		}
		//this.Dump();
		if(this.TokenContext.TokenList.size() > TokenSize) {
			return true;
		}
		return false;
	}

	public final void LogWarning(int Position, String Message) {
		this.Logger.Report(this.FormatErrorMarker("warning", Position, Message));
	}

}
