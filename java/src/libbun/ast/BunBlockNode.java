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

import libbun.ast.decl.BunLetVarNode;
import libbun.parser.BNameSpace;
import libbun.parser.BVisitor;
import libbun.util.BField;
import libbun.util.Nullable;
import libbun.util.Var;

public class BunBlockNode extends AbstractListNode {
	@BField public BNameSpace NullableNameSpace;

	public BunBlockNode(BNode ParentNode, @Nullable BNameSpace NameSpace) {
		super(ParentNode, 0);
		this.NullableNameSpace = NameSpace;
	}

	protected BunBlockNode(BNode ParentNode, @Nullable BNameSpace NameSpace, int Init) {  // call by ZVarNode
		super(ParentNode, Init);
		this.NullableNameSpace = NameSpace;
	}

	public BunBlockNode(BNode ParentNode, @Nullable BNameSpace NameSpace, BunLetVarNode VarNode) {
		super(ParentNode, 1);
		this.NullableNameSpace = NameSpace;
		this.SetNode(0, VarNode);
	}

	@Override public BNode Dup(boolean TypedClone, BNode ParentNode) {
		return this.DupField(TypedClone, new BunBlockNode(ParentNode, this.NullableNameSpace));
	}

	public final BNameSpace GetBlockNameSpace() {
		if(this.NullableNameSpace == null) {
			@Var BNameSpace NameSpace = this.GetNameSpace();
			this.NullableNameSpace = new BNameSpace(NameSpace.Generator, this);
		}
		return this.NullableNameSpace;
	}

	@Override public void Accept(BVisitor Visitor) {
		Visitor.VisitBlockNode(this);
	}

	public final void ReplaceWith(BNode OldNode, BNode NewNode) {
		@Var int i = 0;
		while(i < this.GetAstSize()) {
			if(this.AST[i] == OldNode) {
				this.AST[i] = NewNode;
				this.SetChild(NewNode, BNode._EnforcedParent);
				if(NewNode.HasUntypedNode()) {
					this.HasUntyped = true;
				}
				return;
			}
			i = i + 1;
		}
		//		System.out.println("no replacement");
		assert(OldNode == NewNode);  // this must not happen!!
	}
}