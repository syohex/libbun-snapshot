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

import libbun.ast.decl.BFunctionNode;
import libbun.ast.error.BErrorNode;
import libbun.ast.expression.BGetNameNode;
import libbun.ast.statement.BWhileNode;
import libbun.parser.BNameSpace;
import libbun.parser.BToken;
import libbun.parser.BTypeChecker;
import libbun.parser.BVisitor;
import libbun.type.BFuncType;
import libbun.type.BType;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.Nullable;
import libbun.util.Var;

public abstract class BNode {
	public final static int _AppendIndex = -1;
	public final static int _NestedAppendIndex = -2;
	public final static int _Nop =      -3;
	public final static boolean _EnforcedParent = true;
	public final static boolean _PreservedParent = false;

	@BField public BNode  ParentNode;
	@BField public BToken SourceToken;
	@BField public BNode  AST[];
	@BField public BType	Type = BType.VarType;
	@BField public boolean HasUntyped = true;

	public BNode(@Nullable BNode ParentNode, @Nullable BToken SourceToken, int Size) {
		assert(this != ParentNode);
		this.ParentNode = ParentNode;
		this.SourceToken = SourceToken;
		if(Size > 0) {
			this.AST = BLib._NewNodeArray(Size);
		}
		else {
			this.AST = null;
		}
	}

	public final String GetSourceLocation() {
		if(this.SourceToken != null) {
			return "(" + this.SourceToken.GetFileName() + ":" + this.SourceToken.GetLineNumber() + ")";
		}
		return null;
	}

	@Override public String toString() {
		@Var String Self = "#" + BLib._GetClassName(this);
		if(!this.Type.IsVarType()) {
			Self = Self + ":" + this.Type;
		}
		else {
			Self = Self + ":?";
		}
		if(this.AST != null) {
			@Var int i = 0;
			Self = Self + "[";
			while(i < this.AST.length) {
				if(i > 0) {
					Self = Self + ",";
				}
				if(this.AST[i] == null) {
					Self = Self + "null";
				}
				else {
					if(this.AST[i].ParentNode == this) {
						Self = Self + this.AST[i].toString();
					}
					else {
						Self = Self + "*" + BLib._GetClassName(this.AST[i])+"*";
					}
				}
				i = i + 1;
			}
			Self = Self + "]";
		}
		return Self;
	}

	// AST[]

	public final int GetAstSize() {
		if(this.AST == null) {
			return 0;
		}
		return this.AST.length;
	}

	public final BNode SetChild(BNode Node, boolean EnforcedParent) {
		assert(this != Node);
		if(EnforcedParent || Node.ParentNode == null) {
			Node.ParentNode = this;
		}
		return Node;
	}

	public final BNode SetParent(BNode Node, boolean Enforced) {
		if(Enforced || this.ParentNode == null) {
			this.ParentNode = Node;
		}
		return this;
	}

	public final BNode SetNode(int Index, BNode Node, boolean EnforcedParent) {
		if(Index >= 0) {
			assert(Index < this.GetAstSize());
			this.AST[Index] = this.SetChild(Node, EnforcedParent);
		}
		else if(Index == BNode._AppendIndex) {
			@Var BNode ListNode = this;
			if(ListNode instanceof BListNode) {
				((BListNode)ListNode).Append(Node);
			}
			else {
				assert(ListNode instanceof BListNode);
			}
		}
		return Node;
	}

	public final BNode SetNode(int Index, BNode Node) {
		return this.SetNode(Index, Node, BNode._EnforcedParent);
	}

	public final BType GetAstType(int Index) {
		if(Index < this.AST.length) {
			return this.AST[Index].Type.GetRealType();
		}
		return BType.VoidType;  // to retrieve RecvType
	}

	public final void SetAstType(int Index, BType Type) {
		if(this.AST[Index] != null) {
			this.AST[Index].Type = Type;
		}
	}

	public final BToken GetAstToken(int TokenIndex) {
		if(TokenIndex >= 0 && this.AST[TokenIndex] != null) {
			return this.AST[TokenIndex].SourceToken;
		}
		return this.SourceToken;
	}

	// ParentNode

	public final boolean IsTopLevel() {
		@Var @Nullable BNode Cur = this.ParentNode;
		while(Cur != null) {
			if(Cur instanceof BFunctionNode) {
				return false;
			}
			Cur = Cur.ParentNode;
		}
		return true;
	}

	@Nullable public final BFunctionNode GetDefiningFunctionNode() {
		@Var @Nullable BNode Cur = this;
		while(Cur != null) {
			if(Cur instanceof BFunctionNode) {
				return (BFunctionNode)Cur;
			}
			Cur = Cur.ParentNode;
		}
		return null;
	}

	@Nullable public final BBlockNode GetScopeBlockNode() {
		@Var int SafeCount = 0;
		@Var BNode Node = this;
		while(Node != null) {
			if(Node instanceof BBlockNode) {
				return (BBlockNode)Node;
			}
			assert(!(Node == Node.ParentNode));
			//System.out.println("node: " + Node.getClass() + ", " + Node.hashCode() + ", " + SafeCount);
			Node = Node.ParentNode;
			if(BLib.DebugMode) {
				SafeCount = SafeCount + 1;
				assert(SafeCount < 100);
			}
		}
		return null;
	}

	public final BNameSpace GetNameSpace() {
		@Var int SafeCount = 0;
		@Var BBlockNode BlockNode = this.GetScopeBlockNode();
		while(BlockNode.NullableNameSpace == null) {
			@Var BBlockNode ParentBlockNode = BlockNode.ParentNode.GetScopeBlockNode();
			BlockNode = ParentBlockNode;
			if(BLib.DebugMode) {
				SafeCount = SafeCount + 1;
				assert(SafeCount < 100);
			}
		}
		return BlockNode.NullableNameSpace;
	}

	public final boolean IsErrorNode() {
		return (this instanceof BErrorNode);
	}

	public abstract void Accept(BVisitor Visitor);

	public final boolean IsUntyped() {
		return !(this.Type instanceof BFuncType) && this.Type.IsVarType();
	}

	public final boolean HasUntypedNode() {
		if(this.HasUntyped) {
			if(!this.IsUntyped()) {
				@Var int i = 0;
				while(i < this.GetAstSize()) {
					if(this.AST[i] != null && this.AST[i].HasUntypedNode()) {
						//LibZen._PrintLine("@Untyped " + LibZen._GetClassName(this) + "[" + i + "] " + this.AST[i]);
						return true;
					}
					i = i + 1;
				}
				this.HasUntyped = false;
			}
		}
		return this.HasUntyped;
	}

	// Convenient short cut interface
	public final BGetNameNode SetNewGetNameNode(int Index, BTypeChecker Typer, String Name, BType Type) {
		@Var BGetNameNode Node = Typer.CreateGetNameNode(null, Name, Type);
		this.SetNode(Index, Node);
		return Node;
	}

	public final BBlockNode SetNewBlockNode(int Index, BTypeChecker Typer) {
		@Var BBlockNode Node = Typer.CreateBlockNode(null);
		this.SetNode(Index, Node);
		return Node;
	}

	public final BWhileNode SetNewWhileNode(int Index, BTypeChecker Typer) {
		@Var BWhileNode Node = new BWhileNode(null);
		this.SetNode(Index, Node);
		return Node;

	}

}

