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

import libbun.parser.ZGenerator;
import libbun.parser.ZMacroFunc;
import libbun.parser.ZSyntax;
import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ZVisitor;
import libbun.type.ZFunc;
import libbun.util.BField;
import libbun.util.Var;

public class ZBinaryNode extends BNode {
	public final static int _Left = 0;
	public final static int _Right = 1;
	@BField public ZSyntax Pattern;

	public ZBinaryNode(BNode ParentNode, ZToken SourceToken, BNode Left, ZSyntax Pattern) {
		super(ParentNode, SourceToken, 2);
		this.SetNode(ZBinaryNode._Left, Left);
		assert(Pattern != null);
		this.Pattern = Pattern;
	}

	public final BNode LeftNode() {
		return this.AST[ZBinaryNode._Left ];
	}

	public final BNode RightNode() {
		return this.AST[ZBinaryNode._Right ];
	}


	private final boolean IsRightJoin(BNode Node) {
		if(Node instanceof ZBinaryNode) {
			return this.Pattern.IsRightJoin(((ZBinaryNode)Node).Pattern);
		}
		return false;
	}

	private final BNode RightJoin(BNode ParentNode, ZBinaryNode RightNode) {
		@Var BNode RightLeftNode = RightNode.LeftNode();
		if(this.IsRightJoin(RightLeftNode)) {
			RightNode.SetNode(ZBinaryNode._Left, this.RightJoin(ParentNode, (ZBinaryNode) RightLeftNode));
		}
		else {
			RightNode.SetNode(ZBinaryNode._Left, this);
			this.SetNode(ZBinaryNode._Right, RightLeftNode);
		}
		return RightNode;
	}

	public final BNode AppendParsedRightNode(BNode ParentNode, ZTokenContext TokenContext) {
		@Var BNode RightNode = TokenContext.ParsePattern(ParentNode, "$Expression$", ZTokenContext._Required);
		if(RightNode.IsErrorNode()) {
			return RightNode;
		}
		if(this.IsRightJoin(RightNode)) {
			return this.RightJoin(ParentNode, (ZBinaryNode) RightNode);
		}
		this.SetNode(ZBinaryNode._Right, RightNode);
		return this;
	}

	public final BNode TryMacroNode(ZGenerator Generator) {
		if(!this.GetAstType(ZBinaryNode._Left).IsVarType() && !this.GetAstType(ZBinaryNode._Right).IsVarType()) {
			@Var String Op = this.SourceToken.GetText();
			@Var ZFunc Func = Generator.GetDefinedFunc(Op, this.GetAstType(ZBinaryNode._Left), 2);
			if(Func instanceof ZMacroFunc) {
				@Var ZMacroNode MacroNode = new ZMacroNode(this.ParentNode, this.SourceToken, (ZMacroFunc)Func);
				MacroNode.Append(this.LeftNode());
				MacroNode.Append(this.RightNode());
				return MacroNode;
			}
		}
		return this;
	}

	@Override public void Accept(ZVisitor Visitor) {
		Visitor.VisitBinaryNode(this);
	}


}