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

package libbun.parser;

import libbun.ast.BArrayLiteralNode;
import libbun.ast.BAsmNode;
import libbun.ast.BBlockNode;
import libbun.ast.BBooleanNode;
import libbun.ast.BBreakNode;
import libbun.ast.BCastNode;
import libbun.ast.BDefaultValueNode;
import libbun.ast.BErrorNode;
import libbun.ast.BFloatNode;
import libbun.ast.BFunctionNode;
import libbun.ast.BGetIndexNode;
import libbun.ast.BGetNameNode;
import libbun.ast.BGetterNode;
import libbun.ast.BGroupNode;
import libbun.ast.BIfNode;
import libbun.ast.BIntNode;
import libbun.ast.BLetVarNode;
import libbun.ast.BNewObjectNode;
import libbun.ast.BNullNode;
import libbun.ast.BReturnNode;
import libbun.ast.BSetIndexNode;
import libbun.ast.BSetNameNode;
import libbun.ast.BSetterNode;
import libbun.ast.BStringNode;
import libbun.ast.BThrowNode;
import libbun.ast.BTryNode;
import libbun.ast.BWhileNode;
import libbun.ast.ZClassNode;
import libbun.ast.ZFuncCallNode;
import libbun.ast.ZInstanceOfNode;
import libbun.ast.ZLocalDefinedNode;
import libbun.ast.ZMacroNode;
import libbun.ast.ZMapLiteralNode;
import libbun.ast.ZMethodCallNode;
import libbun.ast.ZSugarNode;
import libbun.ast.ZTopLevelNode;
import libbun.ast.ZVarBlockNode;
import libbun.ast.binary.BBinaryNode;
import libbun.ast.binary.BNotNode;
import libbun.ast.binary.BOrNode;
import libbun.ast.binary.BUnaryNode;
import libbun.ast.binary.BAndNode;
import libbun.ast.binary.ZComparatorNode;

public abstract class BVisitor {
	public abstract void VisitDefaultValueNode(BDefaultValueNode Node);
	public abstract void VisitNullNode(BNullNode Node);
	public abstract void VisitBooleanNode(BBooleanNode Node);
	public abstract void VisitIntNode(BIntNode Node);
	public abstract void VisitFloatNode(BFloatNode Node);
	public abstract void VisitStringNode(BStringNode Node);
	public abstract void VisitArrayLiteralNode(BArrayLiteralNode Node);
	public abstract void VisitMapLiteralNode(ZMapLiteralNode Node);
	public abstract void VisitNewObjectNode(BNewObjectNode Node);
	//	public abstract void VisitNewArrayNode(ZNewArrayNode Node);
	public abstract void VisitGetNameNode(BGetNameNode Node);
	public abstract void VisitSetNameNode(BSetNameNode Node);
	public abstract void VisitGroupNode(BGroupNode Node);
	public abstract void VisitGetterNode(BGetterNode Node);
	public abstract void VisitSetterNode(BSetterNode Node);
	public abstract void VisitGetIndexNode(BGetIndexNode Node);
	public abstract void VisitSetIndexNode(BSetIndexNode Node);
	public abstract void VisitMethodCallNode(ZMethodCallNode Node);
	public abstract void VisitFuncCallNode(ZFuncCallNode Node);
	public abstract void VisitMacroNode(ZMacroNode FuncNode);
	public abstract void VisitUnaryNode(BUnaryNode Node);
	public abstract void VisitNotNode(BNotNode Node);
	public abstract void VisitCastNode(BCastNode Node);
	public abstract void VisitInstanceOfNode(ZInstanceOfNode Node);
	public abstract void VisitBinaryNode(BBinaryNode Node);
	public abstract void VisitComparatorNode(ZComparatorNode Node);
	public abstract void VisitAndNode(BAndNode Node);
	public abstract void VisitOrNode(BOrNode Node);
	public abstract void VisitBlockNode(BBlockNode Node);
	public abstract void VisitVarBlockNode(ZVarBlockNode Node);
	public abstract void VisitIfNode(BIfNode Node);
	public abstract void VisitReturnNode(BReturnNode Node);
	public abstract void VisitWhileNode(BWhileNode Node);
	public abstract void VisitBreakNode(BBreakNode Node);
	public abstract void VisitThrowNode(BThrowNode Node);
	public abstract void VisitTryNode(BTryNode Node);

	public abstract void VisitLetNode(BLetVarNode Node);
	public abstract void VisitFunctionNode(BFunctionNode Node);
	public abstract void VisitClassNode(ZClassNode Node);
	public abstract void VisitAsmNode(BAsmNode Node);
	public abstract void VisitErrorNode(BErrorNode Node);

	public abstract void VisitTopLevelNode(ZTopLevelNode Node);
	public abstract void VisitSugarNode(ZSugarNode Node);
	public abstract void VisitLocalDefinedNode(ZLocalDefinedNode Node);

	public abstract void EnableVisitor();
	public abstract void StopVisitor();
	public abstract boolean IsVisitable();
}