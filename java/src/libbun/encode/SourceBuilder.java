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

import libbun.util.BArray;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.Var;

public final class SourceBuilder {
	@BField public BArray<String> SourceList = new BArray<String>(new String[128]);
	@BField SourceBuilder Parent;
	@BField SourceGenerator Template;
	@BField int IndentLevel = 0;
	@BField String CurrentIndentString = "";

	public SourceBuilder(SourceGenerator Template, SourceBuilder Parent) {
		this.Template = Template;
		this.Parent = Parent;
	}

	public final SourceBuilder Pop() {
		this.Append(this.Template.LineFeed);
		return this.Parent;
	}


	public final void Append(String Source) {
		this.SourceList.add(Source);
	}

	public final void Append(String Text, String Text2) {
		this.SourceList.add(Text);
		this.SourceList.add(Text2);
	}

	public final void Append(String Text, String Text2, String Text3) {
		this.SourceList.add(Text);
		this.SourceList.add(Text2);
		this.SourceList.add(Text3);
	}

	public final void AppendInt(int Value) {
		this.SourceList.add("" + Value);
	}

	public final void AppendQuotedText(String Text) {
		this.SourceList.add(BLib._QuoteString(Text));
	}

	public final void AppendLineFeed() {
		this.SourceList.add(this.Template.LineFeed);
	}

	public final void AppendNewLine() {
		this.SourceList.add(this.Template.LineFeed);
		this.SourceList.add(this.GetIndentString());
	}

	public final void AppendNewLine(String Text) {
		this.AppendNewLine();
		this.Append(Text);
	}

	public final void AppendNewLine(String Text, String Text2) {
		this.AppendNewLine();
		this.Append(Text);
		this.Append(Text2);
	}

	public final void AppendNewLine(String Text, String Text2, String Text3) {
		this.AppendNewLine();
		this.Append(Text);
		this.Append(Text2);
		this.Append(Text3);
	}


	public final void OpenIndent() {
		this.IndentLevel = this.IndentLevel + 1;
		this.CurrentIndentString = null;
	}

	public final void OpenIndent(String Text) {
		if(Text != null && Text.length() > 0) {
			this.Append(Text);
		}
		this.OpenIndent();
	}

	public final void CloseIndent() {
		this.IndentLevel = this.IndentLevel - 1;
		this.CurrentIndentString = null;
		BLib._Assert(this.IndentLevel >= 0);
	}

	public final void CloseIndent(String Text) {
		this.CloseIndent();
		if(Text != null && Text.length() > 0) {
			this.AppendNewLine(Text);
		}
	}



	public final int SetIndentLevel(int IndentLevel) {
		int Level = this.IndentLevel;
		this.IndentLevel = IndentLevel;
		this.CurrentIndentString = null;
		return Level;
	}

	private final String GetIndentString() {
		if (this.CurrentIndentString == null) {
			this.CurrentIndentString = BLib._JoinStrings(this.Template.Tab, this.IndentLevel);
		}
		return this.CurrentIndentString;
	}

	public final boolean EndsWith(String s) {
		@Var int Size = this.SourceList.size();
		if(Size > 0) {
			@Var String Last = this.SourceList.ArrayValues[Size-1];
			if(Last != null && Last.endsWith(s)) {
				return true;
			}
		}
		return false;
	}

	public final void AppendWhiteSpace() {
		@Var int Size = this.SourceList.size();
		if(Size > 0) {
			@Var String Last = this.SourceList.ArrayValues[Size-1];
			if(Last != null && (Last.endsWith(" ") || Last.endsWith("\n") || Last.endsWith("\t"))) {
				return;
			}
		}
		this.SourceList.add(" ");
	}

	public final void AppendWhiteSpace(String Text) {
		this.AppendWhiteSpace();
		this.Append(Text);
	}

	public final void AppendWhiteSpace(String Text, String Text2) {
		this.AppendWhiteSpace();
		this.Append(Text);
		this.Append(Text2);
	}

	//	public final void AppendWhiteSpace(String Text, String Text2, String Text3) {
	//		this.AppendWhiteSpace();
	//		this.Append(Text);
	//		this.Append(Text2);
	//		this.Append(Text3);
	//	}

	//	public final void AppendToken(String Text) {
	//		this.AppendWhiteSpace();
	//		this.SourceList.add(Text);
	//		this.AppendWhiteSpace();
	//	}

	//	public final void AppendBlockComment(String Text) {
	//		if (this.Template.BeginComment != null) {
	//			this.SourceList.add(this.Template.BeginComment);
	//			this.SourceList.add(Text);
	//			this.SourceList.add(this.Template.EndComment);
	//		} else if (this.Template.LineComment != null) {
	//			this.BufferedLineComment = this.BufferedLineComment + this.Template.LineComment + Text;
	//		}
	//	}
	//
	//	public final void AppendCommentLine(String Text) {
	//		if (this.Template.LineComment == null) {
	//			this.SourceList.add(this.Template.BeginComment);
	//			this.SourceList.add(Text);
	//			this.SourceList.add(this.Template.EndComment);
	//		} else {
	//			this.SourceList.add(this.Template.LineComment);
	//			this.SourceList.add(Text);
	//		}
	//		this.SourceList.add(this.Template.LineFeed);
	//	}
	//
	//	public final void Indent() {
	//		this.IndentLevel = this.IndentLevel + 1;
	//		this.CurrentIndentString = null;
	//	}
	//
	//	public final void UnIndent() {
	//		this.IndentLevel = this.IndentLevel - 1;
	//		this.CurrentIndentString = null;
	//		BLib._Assert(this.IndentLevel >= 0);
	//	}


	//	public final void AppendNewLine() {
	//		this.SourceList.add(this.GetIndentString());
	//	}


	//
	//	public final void IndentAndAppend(String Text) {
	//		this.SourceList.add(this.GetIndentString());
	//		this.SourceList.add(Text);
	//	}
	//
	//	public final void AppendParamList(AbstractListNode ParamList, int BeginIdx, int EndIdx) {
	//		@Var int i = BeginIdx;
	//		while(i < EndIdx) {
	//			if (i > BeginIdx) {
	//				this.Append(this.Template.Camma);
	//			}
	//			ParamList.GetListAt(i).Accept(this.Template);
	//			i = i + 1;
	//		}
	//	}

	public final void Clear() {
		this.SourceList.clear(0);
	}

	public final int GetPosition() {
		return this.SourceList.size();
	}

	public final String CopyString(int BeginIndex, int EndIndex) {
		return BLib._SourceBuilderToString(this, BeginIndex, EndIndex);
	}

	//	public final void AppendCode(String Source) {
	//		@Var int StartIndex = 0;
	//		@Var int i = 0;
	//		while(i < Source.length()) {
	//			@Var char ch = BLib._GetChar(Source, i);
	//			if(ch == '\n') {
	//				if(StartIndex < i) {
	//					this.SourceList.add(Source.substring(StartIndex, i));
	//				}
	//				this.AppendNewLine();
	//				StartIndex = i + 1;
	//			}
	//			if(ch == '\t') {
	//				if(StartIndex < i) {
	//					this.SourceList.add(Source.substring(StartIndex, i));
	//				}
	//				this.Append(this.Template.Tab);
	//				StartIndex = i + 1;
	//			}
	//			i = i + 1;
	//		}
	//		if(StartIndex < i) {
	//			this.SourceList.add(Source.substring(StartIndex, i));
	//		}
	//	}

	@Override public final String toString() {
		return BLib._SourceBuilderToString(this);
	}
	////
	////	@Deprecated public final void AppendLine(String Text) {
	////		this.Append(Text);
	////		this.AppendLineFeed();
	//	}

}
