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

package libbun.parser.ast;

import libbun.parser.ZNameSpace;
import libbun.parser.ZVisitor;
import libbun.util.Field;
import libbun.util.Nullable;
import libbun.util.Var;

public class ZBlockNode extends ZListNode {
	@Field public ZNameSpace NullableNameSpace;

	public ZBlockNode(ZNode ParentNode, @Nullable ZNameSpace NameSpace) {
		super(ParentNode, null, 0);
		this.NullableNameSpace = NameSpace;
	}

	protected ZBlockNode(ZNode ParentNode, @Nullable ZNameSpace NameSpace, int Init) {  // call by ZVarNode
		super(ParentNode, null, Init);
		this.NullableNameSpace = NameSpace;
	}

	public ZBlockNode(ZNode ParentNode, @Nullable ZNameSpace NameSpace, ZLetNode VarNode) {
		super(ParentNode, null, 1);
		this.NullableNameSpace = NameSpace;
		this.SetNode(0, VarNode);
	}

	public final ZNameSpace GetBlockNameSpace() {
		if(this.NullableNameSpace == null) {
			@Var ZNameSpace NameSpace = this.GetNameSpace();
			this.NullableNameSpace = new ZNameSpace(NameSpace.Generator, this);
		}
		return this.NullableNameSpace;
	}

	@Override public void Accept(ZVisitor Visitor) {
		Visitor.VisitBlockNode(this);
	}

	public final boolean HasVarDecl() {
		if(this.ListStartIndex == 1) {
			return true;
		}
		return false;
	}

	public final ZLetNode VarNode() {
		@Var ZNode VarNode = this.AST[0];
		if(VarNode instanceof ZLetNode) {
			return (ZLetNode)VarNode;
		}
		return null;
	}

	public final int IndexOf(ZNode ChildNode) {
		@Var int i = 0;
		while(i < this.GetListSize()) {
			if(this.GetListAt(i) == ChildNode) {
				return i;
			}
			i = i + 1;
		}
		return -1;
	}

	public final void CopyTo(int Index, ZBlockNode BlockNode) {
		@Var int i = Index;
		while(i < this.GetListSize()) {
			BlockNode.Append(this.GetListAt(i));
			i = i + 1;
		}
	}

	public final void ReplaceWith(ZNode OldNode, ZNode NewNode) {
		@Var int i = 0;
		while(i < this.GetAstSize()) {
			if(this.AST[i] == OldNode) {
				this.AST[i] = NewNode;
				NewNode.ParentNode = this;
				if(NewNode.HasUntypedNode()) {
					this.HasUntyped = true;
				}
				return;
			}
			i = i + 1;
		}
		System.out.println("no replacement");
		assert(OldNode == NewNode);  // this must not happen!!
	}
}