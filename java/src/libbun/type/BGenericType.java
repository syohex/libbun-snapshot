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

package libbun.type;

import libbun.util.BField;
import libbun.util.Nullable;

public class BGenericType extends BType {
	public final static BType _AlphaType  = new BGreekType(0);

	public final static BGenericType _ArrayType = new BGenericType("Array");
	public final static BGenericType _MapType = new BGenericType("Map");

	@BField public BType			BaseType;
	@BField public BType         ParamType;

	public BGenericType(String ShortName) {
		super(BType.UniqueTypeFlag, ShortName, BType.VarType);
		this.BaseType = this;
		this.ParamType = BGenericType._AlphaType;
		BTypePool._SetBaseGenericType(this);
	}

	public BGenericType(int TypeFlag, @Nullable BType BaseType, BType ParamType) {
		super(TypeFlag, null, BType.VarType);
		this.BaseType = BaseType;
		this.ParamType = ParamType;
	}

	@Override public final String GetName() {
		if(this.ShortName == null) {
			if(this.BaseType.IsArrayType()) {
				this.ShortName = this.ParamType.GetName() + "[]";
			}
			else {
				this.ShortName = this.BaseType.GetName() + "<" + this.ParamType.GetName() + ">";
			}
		}
		return this.ShortName;
	}

	@Override public final BType GetSuperType() {
		if(this.BaseType == this) {
			return this.RefType;
		}
		return this.BaseType;
	}

	@Override public final BType GetBaseType() {
		return this.BaseType;
	}

	@Override public final int GetParamSize() {
		return 1;
	}

	@Override public final BType GetParamType(int Index) {
		if(Index == 0) {
			return this.ParamType;
		}
		return null;
	}

	@Override public final boolean IsGreekType() {
		return (this.ParamType.IsGreekType());
	}

	@Override public final BType GetGreekRealType(BType[] Greek) {
		if(this.ParamType.IsGreekType()) {
			return BTypePool._GetGenericType1(this.BaseType, this.ParamType.GetGreekRealType(Greek));
		}
		return this.GetRealType();
	}

	@Override public final boolean AcceptValueType(BType ValueType, boolean ExactMatch, BType[] Greek) {
		if(this.BaseType == ValueType.GetBaseType() && ValueType.GetParamSize() == 1) {
			return this.ParamType.AcceptValueType(ValueType.GetParamType(0), true, Greek);
		}
		return false;
	}

}
