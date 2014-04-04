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

import libbun.parser.BToken;
import libbun.parser.BVisitor;
import libbun.type.BType;

//E.g., $ExprNode instanceof TypeInfo
public final class ZInstanceOfNode extends BNode {
	public final static int _Left = 0;
	public final static int _TypeInfo = 1;

	public ZInstanceOfNode(BNode ParentNode, BToken Token, BNode LeftNode) {
		super(ParentNode, Token, 2);
		this.SetNode(ZInstanceOfNode._Left, LeftNode);
	}

	public final BNode LeftNode() {
		return this.AST[ZInstanceOfNode._Left ];
	}

	public final BType TargetType() {
		return this.AST[ZInstanceOfNode._TypeInfo].Type;
	}

	@Override public void Accept(BVisitor Visitor) {
		Visitor.VisitInstanceOfNode(this);
	}
}