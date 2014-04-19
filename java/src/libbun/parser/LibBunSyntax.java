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

import libbun.util.BField;
import libbun.util.LibBunSystem;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public final class LibBunSyntax {
	public final static int _BinaryOperator					= 1;
	public final static int _LeftJoin						= 1 << 1;

	@BField public LibBunGamma	              PackageGamma;
	@BField public String		              PatternName;
	@BField public BMatchFunction              MatchFunc;
	@BField public int				          SyntaxFlag = 0;
	@BField public LibBunSyntax              ParentPattern = null;
	@BField public boolean IsDisabled          = false;
	@BField public boolean IsStatement         = false;

	public LibBunSyntax(LibBunGamma Gamma, String PatternName, BMatchFunction MatchFunc) {
		this.PackageGamma = Gamma;
		this.PatternName = PatternName;
		this.MatchFunc = MatchFunc;
	}

	@Override public String toString() {
		return this.PatternName  /* + "{" + this.MatchFunc + "}"*/;
	}

	public final boolean IsBinaryOperator() {
		return LibBunSystem._IsFlag(this.SyntaxFlag, LibBunSyntax._BinaryOperator);
	}

	public final boolean IsRightJoin(LibBunSyntax Right) {
		@Var int left = this.SyntaxFlag;
		@Var int right = Right.SyntaxFlag;
		return (left < right || (left == right && !LibBunSystem._IsFlag(left, LibBunSyntax._LeftJoin) && !LibBunSystem._IsFlag(right, LibBunSyntax._LeftJoin)));
	}

	//	public final boolean EqualsName(String Name) {
	//		return this.PatternName.equals(Name);
	//	}

	public final static LibBunSyntax MergeSyntaxPattern(LibBunSyntax Pattern, LibBunSyntax Parent) {
		if(Parent == null) {
			return Pattern;
		}
		@Var LibBunSyntax MergedPattern = new LibBunSyntax(Pattern.PackageGamma, Pattern.PatternName, Pattern.MatchFunc);
		MergedPattern.ParentPattern = Parent;
		return MergedPattern;
	}

}
