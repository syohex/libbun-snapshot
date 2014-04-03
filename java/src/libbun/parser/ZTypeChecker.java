//***************************************************************************
//Copyright (c) 2013-2014, Libbun project authors. All rights reserved.
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
//*  Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//*  Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
//"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
//TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
//PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
//CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
//EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
//PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
//OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
//WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
//OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
//ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//**************************************************************************

package libbun.parser;

import libbun.parser.ast.BGetNameNode;
import libbun.parser.ast.BLetVarNode;
import libbun.parser.ast.BNode;
import libbun.parser.ast.BAsmNode;
import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.ZCastNode;
import libbun.parser.ast.ZDesugarNode;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.ZFuncCallNode;
import libbun.parser.ast.ZFuncNameNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.ZListNode;
import libbun.parser.ast.ZMacroNode;
import libbun.parser.ast.ZReturnNode;
import libbun.parser.ast.ZStupidCastErrorNode;
import libbun.parser.ast.ZSugarNode;
import libbun.parser.ast.ZVarBlockNode;
import libbun.type.ZFunc;
import libbun.type.ZFuncType;
import libbun.type.ZGreekType;
import libbun.type.ZType;
import libbun.type.ZVarScope;
import libbun.util.Field;
import libbun.util.LibZen;
import libbun.util.Var;

public abstract class ZTypeChecker extends ZVisitor {
	public final static int _DefaultTypeCheckPolicy			= 0;
	public final static int _NoCheckPolicy                  = 1;

	@Field private ZType      StackedContextType;
	@Field private BNode      ReturnedNode;

	@Field public ZGenerator  Generator;
	@Field public ZLogger     Logger;
	@Field private boolean    StoppedVisitor;
	@Field public ZVarScope   VarScope;
	@Field public boolean     IsSupportNullable = false;
	@Field public boolean     IsSupportMutable  = false;

	public ZTypeChecker(ZGenerator Generator) {
		this.Generator = Generator;
		this.Logger = Generator.Logger;
		this.StackedContextType = null;
		this.ReturnedNode = null;
		this.StoppedVisitor = false;
		this.VarScope = new ZVarScope(null, this.Logger, null);
	}

	@Override public final void EnableVisitor() {
		this.StoppedVisitor = false;
	}

	@Override public final void StopVisitor() {
		this.StoppedVisitor = true;
	}

	@Override public final boolean IsVisitable() {
		return !this.StoppedVisitor;
	}

	public final ZType GetContextType() {
		return this.StackedContextType;
	}

	public final void ReturnNode(BNode Node) {
		if(this.ReturnedNode != null) {
			LibZen._PrintDebug("previous returned node " + Node);
		}
		this.ReturnedNode = Node;
	}

	public final void ReturnTypeNode(BNode Node, ZType Type) {
		this.VarScope.TypeNode(Node, Type);
		this.ReturnNode(Node);
	}

	public final void ReturnErrorNode(BNode Node, ZToken ErrorToken, String Message) {
		if(ErrorToken == null) {
			ErrorToken = Node.SourceToken;
		}
		this.ReturnNode(new ZErrorNode(Node.ParentNode, ErrorToken, Message));
	}

	protected final BNode CreateStupidCastNode(ZType Requested, BNode Node, ZToken SourceToken, String Message) {
		@Var BNode ErrorNode = new ZStupidCastErrorNode(Node, SourceToken,  Message + ": " + Node.Type.GetName() + " => " + Requested.GetName());
		ErrorNode.Type = Requested;
		return ErrorNode;
	}

	protected final BNode CreateStupidCastNode(ZType Requested, BNode Node) {
		return this.CreateStupidCastNode(Requested, Node, Node.SourceToken, "type error");
	}

	protected final BNode EnforceNodeType(BNode Node, ZType EnforcedType) {
		@Var ZFunc Func = this.Generator.LookupConverterFunc(Node.Type, EnforcedType);
		if(Func == null && EnforcedType.IsStringType()) {
			Func = this.Generator.LookupFunc("toString", Node.Type, 1);
		}
		if(Func != null) {
			@Var ZListNode FuncNode = this.CreateDefinedFuncCallNode(Node.ParentNode, null, Func);
			FuncNode.Append(Node);
			return this.TypeListNodeAsFuncCall(FuncNode, Func.GetFuncType());
		}
		return this.CreateStupidCastNode(EnforcedType, Node);
	}

	private final BNode TypeCheckImpl(BNode Node, ZType ContextType, int TypeCheckPolicy) {
		if(Node.IsErrorNode()) {
			if(!ContextType.IsVarType()) {
				this.VarScope.TypeNode(Node, ContextType);
			}
			return Node;
		}
		if(Node.IsUntyped() || ContextType.IsVarType() || LibZen._IsFlag(TypeCheckPolicy, ZTypeChecker._NoCheckPolicy)) {
			return Node;
		}
		if(Node.Type == ContextType || ContextType.Accept(Node.Type)) {
			return Node;
		}
		if(ContextType.IsVoidType() && !Node.Type.IsVoidType()) {
			return new ZCastNode(Node.ParentNode, ZType.VoidType, Node);
		}
		if(ContextType.IsFloatType() && Node.Type.IsIntType()) {
			return this.EnforceNodeType(Node, ContextType);
		}
		if(ContextType.IsIntType() && Node.Type.IsFloatType()) {
			return this.EnforceNodeType(Node, ContextType);
		}
		return this.CreateStupidCastNode(ContextType, Node);
	}

	private BNode VisitNode(BNode Node, ZType ContextType) {
		@Var BNode ParentNode = Node.ParentNode;
		this.StackedContextType = ContextType;
		this.ReturnedNode = null;
		Node.Accept(this);
		if(this.ReturnedNode == null) {  /* debug check */
			LibZen._PrintDebug("!! returns no value: " + Node);
		}
		else {
			Node = this.ReturnedNode;
		}
		if(ParentNode != Node.ParentNode && ParentNode != null) {
			if(Node.ParentNode != null) {
				LibZen._PrintDebug("Preserving parent of typed new node: " + Node);
			}
			ParentNode.SetChild(Node, BNode._PreservedParent);
		}
		return Node;
	}

	private final BNode TypeCheck(BNode Node, ZType ContextType, int TypeCheckPolicy) {
		if(this.IsVisitable() && Node != null) {
			if(Node.HasUntypedNode()) {
				Node = this.VisitNode(Node, ContextType);
				this.VarScope.InferType(ContextType, Node);
			}
			Node = this.TypeCheckImpl(Node, ContextType, TypeCheckPolicy);
			this.VarScope.InferType(ContextType, Node);
		}
		this.ReturnedNode = null;
		return Node;
	}


	public final BNode TryType(BNode Node, ZType ContextType) {
		return this.TypeCheck(Node, ContextType, ZTypeChecker._NoCheckPolicy);
	}

	public final void TryTypeAt(BNode Node, int Index, ZType ContextType) {
		//		@Var ZNode N = Node.AST[Index];
		Node.SetNode(Index, this.TypeCheck(Node.AST[Index], ContextType, ZTypeChecker._NoCheckPolicy));
		//		if(N != Node.AST[Index]) {
		//			System.out.println("Node="+Node+"\n\tFrom="+N+"\n\tTo="+Node.AST[Index]);
		//		}
	}

	public final BNode CheckType(BNode Node, ZType ContextType) {
		return this.TypeCheck(Node, ContextType, ZTypeChecker._DefaultTypeCheckPolicy);
	}

	public final void CheckTypeAt(BNode Node, int Index, ZType ContextType) {
		//		@Var ZNode N = Node.AST[Index];
		Node.SetNode(Index, this.TypeCheck(Node.AST[Index], ContextType, ZTypeChecker._DefaultTypeCheckPolicy));
		//		if(N != Node.AST[Index]) {
		//			System.out.println("Node="+Node+"\n\tFrom="+N+"\n\tTo="+Node.AST[Index]);
		//		}
	}

	public final void TypeCheckNodeList(ZListNode List) {
		@Var int i = 0;
		while(i < List.GetListSize()) {
			@Var BNode SubNode = List.GetListAt(i);
			SubNode = this.CheckType(SubNode, ZType.VarType);
			List.SetListAt(i, SubNode);
			i = i + 1;
		}
	}

	public final BNode TypeListNodeAsFuncCall(ZListNode FuncNode, ZFuncType FuncType) {
		@Var int i = 0;
		@Var ZType[] Greek = ZGreekType._NewGreekTypes(null);
		//		if(FuncNode.GetListSize() != FuncType.GetFuncParamSize()) {
		//			System.err.println(ZLogger._LogError(FuncNode.SourceToken, "mismatch " + FuncType + ", " + FuncNode.GetListSize()+": " + FuncNode));
		//		}
		while(i < FuncNode.GetListSize()) {
			@Var BNode SubNode = FuncNode.GetListAt(i);
			@Var ZType ParamType =  FuncType.GetFuncParamType(i);
			SubNode = this.TryType(SubNode, ParamType);
			if(!SubNode.IsUntyped() || !ParamType.IsVarType()) {
				if(!ParamType.AcceptValueType(SubNode.Type, false, Greek)) {
					SubNode = this.CreateStupidCastNode(ParamType.GetGreekRealType(Greek), SubNode);
				}
			}
			FuncNode.SetListAt(i, SubNode);
			i = i + 1;
		}
		this.VarScope.TypeNode(FuncNode, FuncType.GetReturnType().GetGreekRealType(Greek));
		return FuncNode;
	}


	public abstract void DefineFunction(ZFunctionNode FunctionNode, boolean Enforced);

	@Override public void VisitErrorNode(ZErrorNode Node) {
		@Var ZType ContextType = this.GetContextType();
		if(!ContextType.IsVarType()) {
			this.ReturnTypeNode(Node, ContextType);
		}
		else {
			this.ReturnNode(Node);
		}
	}

	@Override public void VisitSugarNode(ZSugarNode Node) {
		@Var ZType ContextType = this.GetContextType();
		@Var ZDesugarNode DesugarNode = Node.DeSugar(this.Generator, this.Generator.TypeChecker);
		@Var int i = 0;
		while(i < DesugarNode.GetAstSize()) {
			this.CheckTypeAt(DesugarNode, i, ContextType);
			i = i + 1;
		}
		this.ReturnTypeNode(DesugarNode, DesugarNode.GetAstType(DesugarNode.GetAstSize()-1));
	}

	@Override public void VisitAsmNode(BAsmNode Node) {
		this.ReturnTypeNode(Node, Node.MacroType());
	}

	// ----------------------------------------------------------------------
	/* Note : the CreateNode serise are designed to treat typed node */

	public ZFunctionNode CreateFunctionNode(BNode ParentNode, String FuncName, BNode Node) {
		@Var ZFunctionNode FuncNode = new ZFunctionNode(ParentNode);
		FuncNode.GivenName = FuncName;
		FuncNode.GivenType = Node.Type;
		@Var ZBlockNode BlockNode = this.CreateBlockNode(FuncNode);
		FuncNode.SetNode(ZFunctionNode._Block, BlockNode);
		if(Node.Type.IsVoidType()) {
			BlockNode.Append(Node);
			BlockNode.Append(this.CreateReturnNode(BlockNode));
		}
		else {
			BlockNode.Append(this.CreateReturnNode(BlockNode, Node));
		}
		FuncNode.Type = ZType.VoidType;
		return FuncNode;
	}

	public ZBlockNode CreateBlockNode(BNode ParentNode) {
		@Var ZBlockNode BlockNode = new ZBlockNode(ParentNode, null);
		BlockNode.Type = ZType.VoidType;
		return BlockNode;
	}

	public ZReturnNode CreateReturnNode(BNode ParentNode) {
		@Var ZReturnNode ReturnNode = new ZReturnNode(ParentNode);
		ReturnNode.Type = ZType.VoidType;
		return ReturnNode;
	}

	public ZReturnNode CreateReturnNode(BNode ParentNode, BNode ExprNode) {
		@Var ZReturnNode ReturnNode = new ZReturnNode(ParentNode);
		ReturnNode.SetNode(ZReturnNode._Expr, ExprNode);
		ReturnNode.Type = ZType.VoidType;
		return ReturnNode;
	}

	public ZVarBlockNode CreateVarNode(BNode ParentNode, String Name, ZType DeclType, BNode InitNode) {
		@Var BLetVarNode VarNode = new BLetVarNode(null, 0, null, null);
		VarNode.GivenName   = Name;
		VarNode.GivenType   = DeclType;
		VarNode.SetNode(BLetVarNode._InitValue, InitNode);
		VarNode.Type = ZType.VoidType;
		return new ZVarBlockNode(ParentNode, VarNode);
	}

	public BGetNameNode CreateGetNameNode(BNode ParentNode, String Name, ZType Type) {
		@Var BGetNameNode NameNode = new BGetNameNode(ParentNode, null, Name);
		NameNode.Type = Type;
		return NameNode;
	}

	public ZFuncCallNode CreateFuncCallNode(BNode ParentNode, ZToken SourceToken, String FuncName, ZFuncType FuncType) {
		@Var ZFuncCallNode FuncNode = new ZFuncCallNode(ParentNode, new ZFuncNameNode(null, SourceToken, FuncName, FuncType));
		FuncNode.Type = FuncType.GetReturnType();
		return FuncNode;
	}

	public final ZListNode CreateDefinedFuncCallNode(BNode ParentNode, ZToken SourceToken, ZFunc Func) {
		@Var ZListNode FuncNode = null;
		if(Func instanceof ZMacroFunc) {
			FuncNode = new ZMacroNode(ParentNode, SourceToken, (ZMacroFunc)Func);
		}
		else {
			FuncNode = this.CreateFuncCallNode(ParentNode, SourceToken, Func.FuncName, Func.GetFuncType());
		}
		//		FuncNode.Type = Func.GetFuncType().GetRealType();
		return FuncNode;
	}

}

