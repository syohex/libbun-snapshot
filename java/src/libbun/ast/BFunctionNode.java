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

package libbun.ast;

import libbun.parser.BGenerator;
import libbun.parser.BVisitor;
import libbun.type.BFuncType;
import libbun.type.BType;
import libbun.type.BTypePool;
import libbun.util.BField;
import libbun.util.Var;
import libbun.util.BArray;

public class BFunctionNode extends BListNode {
	public static final int _NameInfo = 0;
	public static final int _TypeInfo = 1;
	public final static int _Block    = 2;

	@BField public BType  GivenType = null;
	@BField public String GivenName = null;

	@BField public boolean       IsExport = false;
	@BField public BFunctionNode ParentFunctionNode = null;
	@BField public BFuncType     ResolvedFuncType = null;

	public BFunctionNode(BNode ParentNode) {
		super(ParentNode, null, 3);
	}

	public final BType ReturnType() {
		if(this.GivenType == null) {
			if(this.AST[BFunctionNode._TypeInfo] != null) {
				this.GivenType = this.AST[BFunctionNode._TypeInfo].Type;
			}
			else {
				this.GivenType = BType.VarType;
			}
		}
		return this.GivenType;
	}

	public final void SetReturnType(BType Type) {
		this.GivenType = Type;
	}

	public final String FuncName() {
		if(this.GivenName == null && this.AST[BFunctionNode._NameInfo] != null) {
			this.GivenName = this.AST[BFunctionNode._NameInfo].SourceToken.GetTextAsName();
		}
		return this.GivenName;
	}

	public final String GetUniqueName(BGenerator Generator) {
		@Var String FuncName = this.FuncName();
		if(FuncName == null) {
			FuncName = "f";
		}
		return Generator.NameUniqueSymbol(FuncName);
	}

	public final BBlockNode BlockNode() {
		@Var BNode BlockNode = this.AST[BFunctionNode._Block];
		if(BlockNode instanceof BBlockNode) {
			return (BBlockNode)BlockNode;
		}
		assert(BlockNode == null); // this must not happen
		return null;
	}

	@Override public void Accept(BVisitor Visitor) {
		Visitor.VisitFunctionNode(this);
	}

	public final BLetVarNode GetParamNode(int Index) {
		@Var BNode Node = this.GetListAt(Index);
		if(Node instanceof BLetVarNode) {
			return (BLetVarNode)Node;
		}
		return null;
	}

	public final BFuncType GetFuncType() {
		if(this.ResolvedFuncType == null) {
			@Var BArray<BType> TypeList = new BArray<BType>(new BType[this.GetListSize()+2]);
			@Var int i = 0;
			while(i < this.GetListSize()) {
				@Var BLetVarNode Node = this.GetParamNode(i);
				@Var BType ParamType = Node.DeclType().GetRealType();
				TypeList.add(ParamType);
				i = i + 1;
			}
			TypeList.add(this.ReturnType().GetRealType());
			@Var BFuncType FuncType = BTypePool._LookupFuncType2(TypeList);
			if(!FuncType.IsVarType()) {
				this.ResolvedFuncType = FuncType;
			}
			return FuncType;
		}
		return this.ResolvedFuncType;
	}

	public final String GetSignature() {
		@Var BFuncType FuncType = this.GetFuncType();
		return FuncType.StringfySignature(this.FuncName());
	}

	public final BFunctionNode Push(BFunctionNode Parent) {
		this.ParentFunctionNode = Parent;
		return this;
	}

	public final BFunctionNode Pop() {
		return this.ParentFunctionNode;
	}

	public final boolean IsTopLevelDefineFunction() {
		return (this.Type.IsVoidType());
	}


}