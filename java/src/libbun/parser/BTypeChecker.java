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

import libbun.ast.BBlockNode;
import libbun.ast.BListNode;
import libbun.ast.BNode;
import libbun.ast.ZDesugarNode;
import libbun.ast.ZSugarNode;
import libbun.ast.decl.BFunctionNode;
import libbun.ast.decl.BLetVarNode;
import libbun.ast.decl.ZVarBlockNode;
import libbun.ast.error.BErrorNode;
import libbun.ast.error.ZStupidCastErrorNode;
import libbun.ast.expression.BFuncCallNode;
import libbun.ast.expression.BFuncNameNode;
import libbun.ast.expression.BGetNameNode;
import libbun.ast.expression.BMacroNode;
import libbun.ast.literal.BAsmNode;
import libbun.ast.statement.BReturnNode;
import libbun.ast.unary.BCastNode;
import libbun.type.BFunc;
import libbun.type.BFuncType;
import libbun.type.BGreekType;
import libbun.type.BMacroFunc;
import libbun.type.BType;
import libbun.type.BVarScope;
import libbun.util.BField;
import libbun.util.BLib;
import libbun.util.Var;

public abstract class BTypeChecker extends BVisitor {
	public final static int _DefaultTypeCheckPolicy			= 0;
	public final static int _NoCheckPolicy                  = 1;

	@BField private BType      StackedContextType;
	@BField private BNode      ReturnedNode;

	@BField public BGenerator  Generator;
	@BField public BLogger     Logger;
	@BField private boolean    StoppedVisitor;
	@BField public BVarScope   VarScope;
	@BField public boolean     IsSupportNullable = false;
	@BField public boolean     IsSupportMutable  = false;

	public BTypeChecker(BGenerator Generator) {
		this.Generator = Generator;
		this.Logger = Generator.Logger;
		this.StackedContextType = null;
		this.ReturnedNode = null;
		this.StoppedVisitor = false;
		this.VarScope = new BVarScope(null, this.Logger, null);
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

	public final BType GetContextType() {
		return this.StackedContextType;
	}

	public final void ReturnNode(BNode Node) {
		if(this.ReturnedNode != null) {
			BLib._PrintDebug("previous returned node " + Node);
		}
		this.ReturnedNode = Node;
	}

	public final void ReturnTypeNode(BNode Node, BType Type) {
		this.VarScope.TypeNode(Node, Type);
		this.ReturnNode(Node);
	}

	public final void ReturnErrorNode(BNode Node, BToken ErrorToken, String Message) {
		if(ErrorToken == null) {
			ErrorToken = Node.SourceToken;
		}
		this.ReturnNode(new BErrorNode(Node.ParentNode, ErrorToken, Message));
	}

	protected final BNode CreateStupidCastNode(BType Requested, BNode Node, BToken SourceToken, String Message) {
		@Var BNode ErrorNode = new ZStupidCastErrorNode(Node, SourceToken,  Message + ": " + Node.Type.GetName() + " => " + Requested.GetName());
		ErrorNode.Type = Requested;
		return ErrorNode;
	}

	protected final BNode CreateStupidCastNode(BType Requested, BNode Node) {
		return this.CreateStupidCastNode(Requested, Node, Node.SourceToken, "type error");
	}

	protected final BNode EnforceNodeType(BNode Node, BType EnforcedType) {
		@Var BFunc Func = this.Generator.LookupConverterFunc(Node.Type, EnforcedType);
		if(Func == null && EnforcedType.IsStringType()) {
			Func = this.Generator.LookupFunc("toString", Node.Type, 1);
		}
		if(Func != null) {
			@Var BListNode FuncNode = this.CreateDefinedFuncCallNode(Node.ParentNode, null, Func);
			FuncNode.Append(Node);
			return this.TypeListNodeAsFuncCall(FuncNode, Func.GetFuncType());
		}
		return this.CreateStupidCastNode(EnforcedType, Node);
	}

	private final BNode TypeCheckImpl(BNode Node, BType ContextType, int TypeCheckPolicy) {
		if(Node.IsErrorNode()) {
			if(!ContextType.IsVarType()) {
				this.VarScope.TypeNode(Node, ContextType);
			}
			return Node;
		}
		if(Node.IsUntyped() || ContextType.IsVarType() || BLib._IsFlag(TypeCheckPolicy, BTypeChecker._NoCheckPolicy)) {
			return Node;
		}
		if(Node.Type == ContextType || ContextType.Accept(Node.Type)) {
			return Node;
		}
		if(ContextType.IsVoidType() && !Node.Type.IsVoidType()) {
			return new BCastNode(Node.ParentNode, BType.VoidType, Node);
		}
		if(ContextType.IsFloatType() && Node.Type.IsIntType()) {
			return this.EnforceNodeType(Node, ContextType);
		}
		if(ContextType.IsIntType() && Node.Type.IsFloatType()) {
			return this.EnforceNodeType(Node, ContextType);
		}
		return this.CreateStupidCastNode(ContextType, Node);
	}

	private BNode VisitNode(BNode Node, BType ContextType) {
		@Var BNode ParentNode = Node.ParentNode;
		this.StackedContextType = ContextType;
		this.ReturnedNode = null;
		Node.Accept(this);
		if(this.ReturnedNode == null) {  /* debug check */
			BLib._PrintDebug("!! returns no value: " + Node);
		}
		else {
			Node = this.ReturnedNode;
		}
		if(ParentNode != Node.ParentNode && ParentNode != null) {
			if(Node.ParentNode != null) {
				BLib._PrintDebug("Preserving parent of typed new node: " + Node);
			}
			ParentNode.SetChild(Node, BNode._PreservedParent);
		}
		return Node;
	}

	private final BNode TypeCheck(BNode Node, BType ContextType, int TypeCheckPolicy) {
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


	public final BNode TryType(BNode Node, BType ContextType) {
		return this.TypeCheck(Node, ContextType, BTypeChecker._NoCheckPolicy);
	}

	public final void TryTypeAt(BNode Node, int Index, BType ContextType) {
		//		@Var ZNode N = Node.AST[Index];
		Node.SetNode(Index, this.TypeCheck(Node.AST[Index], ContextType, BTypeChecker._NoCheckPolicy));
		//		if(N != Node.AST[Index]) {
		//			System.out.println("Node="+Node+"\n\tFrom="+N+"\n\tTo="+Node.AST[Index]);
		//		}
	}

	public final BNode CheckType(BNode Node, BType ContextType) {
		return this.TypeCheck(Node, ContextType, BTypeChecker._DefaultTypeCheckPolicy);
	}

	public final void CheckTypeAt(BNode Node, int Index, BType ContextType) {
		//		@Var ZNode N = Node.AST[Index];
		Node.SetNode(Index, this.TypeCheck(Node.AST[Index], ContextType, BTypeChecker._DefaultTypeCheckPolicy));
		//		if(N != Node.AST[Index]) {
		//			System.out.println("Node="+Node+"\n\tFrom="+N+"\n\tTo="+Node.AST[Index]);
		//		}
	}

	public final void TypeCheckNodeList(BListNode List) {
		@Var int i = 0;
		while(i < List.GetListSize()) {
			@Var BNode SubNode = List.GetListAt(i);
			SubNode = this.CheckType(SubNode, BType.VarType);
			List.SetListAt(i, SubNode);
			i = i + 1;
		}
	}

	public final BNode TypeListNodeAsFuncCall(BListNode FuncNode, BFuncType FuncType) {
		@Var int i = 0;
		@Var BType[] Greek = BGreekType._NewGreekTypes(null);
		//		if(FuncNode.GetListSize() != FuncType.GetFuncParamSize()) {
		//			System.err.println(ZLogger._LogError(FuncNode.SourceToken, "mismatch " + FuncType + ", " + FuncNode.GetListSize()+": " + FuncNode));
		//		}
		while(i < FuncNode.GetListSize()) {
			@Var BNode SubNode = FuncNode.GetListAt(i);
			@Var BType ParamType =  FuncType.GetFuncParamType(i);
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


	public abstract void DefineFunction(BFunctionNode FunctionNode, boolean Enforced);

	@Override public void VisitErrorNode(BErrorNode Node) {
		@Var BType ContextType = this.GetContextType();
		if(!ContextType.IsVarType()) {
			this.ReturnTypeNode(Node, ContextType);
		}
		else {
			this.ReturnNode(Node);
		}
	}

	@Override public void VisitSugarNode(ZSugarNode Node) {
		@Var BType ContextType = this.GetContextType();
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

	public BFunctionNode CreateFunctionNode(BNode ParentNode, String FuncName, BNode Node) {
		@Var BFunctionNode FuncNode = new BFunctionNode(ParentNode);
		FuncNode.GivenName = FuncName;
		FuncNode.GivenType = Node.Type;
		@Var BBlockNode BlockNode = this.CreateBlockNode(FuncNode);
		FuncNode.SetNode(BFunctionNode._Block, BlockNode);
		if(Node.Type.IsVoidType()) {
			BlockNode.Append(Node);
			BlockNode.Append(this.CreateReturnNode(BlockNode));
		}
		else {
			BlockNode.Append(this.CreateReturnNode(BlockNode, Node));
		}
		FuncNode.Type = BType.VoidType;
		return FuncNode;
	}

	public BBlockNode CreateBlockNode(BNode ParentNode) {
		@Var BBlockNode BlockNode = new BBlockNode(ParentNode, null);
		BlockNode.Type = BType.VoidType;
		return BlockNode;
	}

	public BReturnNode CreateReturnNode(BNode ParentNode) {
		@Var BReturnNode ReturnNode = new BReturnNode(ParentNode);
		ReturnNode.Type = BType.VoidType;
		return ReturnNode;
	}

	public BReturnNode CreateReturnNode(BNode ParentNode, BNode ExprNode) {
		@Var BReturnNode ReturnNode = new BReturnNode(ParentNode);
		ReturnNode.SetNode(BReturnNode._Expr, ExprNode);
		ReturnNode.Type = BType.VoidType;
		return ReturnNode;
	}

	public ZVarBlockNode CreateVarNode(BNode ParentNode, String Name, BType DeclType, BNode InitNode) {
		@Var BLetVarNode VarNode = new BLetVarNode(null, 0, null, null);
		VarNode.GivenName   = Name;
		VarNode.GivenType   = DeclType;
		VarNode.SetNode(BLetVarNode._InitValue, InitNode);
		VarNode.Type = BType.VoidType;
		return new ZVarBlockNode(ParentNode, VarNode);
	}

	public BGetNameNode CreateGetNameNode(BNode ParentNode, String Name, BType Type) {
		@Var BGetNameNode NameNode = new BGetNameNode(ParentNode, null, Name);
		NameNode.Type = Type;
		return NameNode;
	}

	public BFuncCallNode CreateFuncCallNode(BNode ParentNode, BToken SourceToken, String FuncName, BFuncType FuncType) {
		@Var BFuncCallNode FuncNode = new BFuncCallNode(ParentNode, new BFuncNameNode(null, SourceToken, FuncName, FuncType));
		FuncNode.Type = FuncType.GetReturnType();
		return FuncNode;
	}

	public final BListNode CreateDefinedFuncCallNode(BNode ParentNode, BToken SourceToken, BFunc Func) {
		@Var BListNode FuncNode = null;
		if(Func instanceof BMacroFunc) {
			FuncNode = new BMacroNode(ParentNode, SourceToken, (BMacroFunc)Func);
		}
		else {
			FuncNode = this.CreateFuncCallNode(ParentNode, SourceToken, Func.FuncName, Func.GetFuncType());
		}
		//		FuncNode.Type = Func.GetFuncType().GetRealType();
		return FuncNode;
	}

}

