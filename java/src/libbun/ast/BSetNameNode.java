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
import libbun.util.Var;

// E.g., $NativeName = $ValueNode
public class BSetNameNode extends BNode {
	public final static int _NameInfo = 0;
	public final static int _Expr = 0;

	public BSetNameNode(BNode ParentNode, BToken Token, BGetNameNode NameNode) {
		super(ParentNode, Token, 2);
		this.SetNode(BSetNameNode._NameInfo, NameNode);
	}

	public BSetNameNode(String Name, BNode ExprNode) {
		this(null, null, new BGetNameNode(null, null, Name));
		this.SetNode(BSetNameNode._Expr, ExprNode);
		if(!ExprNode.IsUntyped()) {
			this.Type = BType.VoidType;
		}
	}

	//	public final String GetName() {
	//		return this.GivenName;
	//	}

	public final BGetNameNode NameNode() {
		@Var BNode NameNode = this.AST[BSetNameNode._NameInfo ];
		if(NameNode instanceof BGetNameNode) {
			return (BGetNameNode)NameNode;
		}
		return null;
	}

	public final BNode ExprNode() {
		return this.AST[BSetNameNode._Expr ];
	}

	@Override public void Accept(BVisitor Visitor) {
		Visitor.VisitSetNameNode(this);
	}
}