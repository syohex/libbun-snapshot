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

import libbun.parser.BVisitor;

//E.g., $Recv[$Index] = $ValueNode
public final class BSetIndexNode extends BNode {
	public final static int _Recv  = 0;
	public final static int _Index = 1;
	public final static int _Expr  = 2;

	public BSetIndexNode(BNode ParentNode, BNode LeftNode) {
		super(ParentNode, null, 3);
		this.SetNode(BSetIndexNode._Recv, LeftNode);
	}
	public final BNode RecvNode() {
		return this.AST[BSetIndexNode._Recv ];
	}
	public final BNode IndexNode() {
		return this.AST[BSetIndexNode._Index ];
	}
	public final BNode ExprNode() {
		return this.AST[BSetIndexNode._Expr ];
	}

	@Override public void Accept(BVisitor Visitor) {
		Visitor.VisitSetIndexNode(this);
	}
}