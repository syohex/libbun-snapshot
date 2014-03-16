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

import libbun.parser.ast.ZAndNode;
import libbun.parser.ast.ZArrayLiteralNode;
import libbun.parser.ast.ZAsmNode;
import libbun.parser.ast.ZBinaryNode;
import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.ZBooleanNode;
import libbun.parser.ast.ZBreakNode;
import libbun.parser.ast.ZCastNode;
import libbun.parser.ast.ZClassNode;
import libbun.parser.ast.ZComparatorNode;
import libbun.parser.ast.ZDefaultValueNode;
import libbun.parser.ast.ZErrorNode;
import libbun.parser.ast.ZFloatNode;
import libbun.parser.ast.ZFuncCallNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.ZGetIndexNode;
import libbun.parser.ast.ZGetNameNode;
import libbun.parser.ast.ZGetterNode;
import libbun.parser.ast.ZGlobalNameNode;
import libbun.parser.ast.ZGroupNode;
import libbun.parser.ast.ZIfNode;
import libbun.parser.ast.ZInstanceOfNode;
import libbun.parser.ast.ZIntNode;
import libbun.parser.ast.ZLetNode;
import libbun.parser.ast.ZLocalDefinedNode;
import libbun.parser.ast.ZMacroNode;
import libbun.parser.ast.ZMapLiteralNode;
import libbun.parser.ast.ZMethodCallNode;
import libbun.parser.ast.ZNewObjectNode;
import libbun.parser.ast.ZNotNode;
import libbun.parser.ast.ZNullNode;
import libbun.parser.ast.ZOrNode;
import libbun.parser.ast.ZReturnNode;
import libbun.parser.ast.ZSetIndexNode;
import libbun.parser.ast.ZSetNameNode;
import libbun.parser.ast.ZSetterNode;
import libbun.parser.ast.ZStringNode;
import libbun.parser.ast.ZSugarNode;
import libbun.parser.ast.ZThrowNode;
import libbun.parser.ast.ZTopLevelNode;
import libbun.parser.ast.ZTryNode;
import libbun.parser.ast.ZUnaryNode;
import libbun.parser.ast.ZVarNode;
import libbun.parser.ast.ZWhileNode;

public abstract class ZVisitor {
	public abstract void VisitDefaultValueNode(ZDefaultValueNode Node);
	public abstract void VisitNullNode(ZNullNode Node);
	public abstract void VisitBooleanNode(ZBooleanNode Node);
	public abstract void VisitIntNode(ZIntNode Node);
	public abstract void VisitFloatNode(ZFloatNode Node);
	public abstract void VisitStringNode(ZStringNode Node);
	public abstract void VisitArrayLiteralNode(ZArrayLiteralNode Node);
	public abstract void VisitMapLiteralNode(ZMapLiteralNode Node);
	public abstract void VisitNewObjectNode(ZNewObjectNode Node);
	//	public abstract void VisitNewArrayNode(ZNewArrayNode Node);
	public abstract void VisitGlobalNameNode(ZGlobalNameNode Node);
	public abstract void VisitGetNameNode(ZGetNameNode Node);
	public abstract void VisitSetNameNode(ZSetNameNode Node);
	public abstract void VisitGroupNode(ZGroupNode Node);
	public abstract void VisitGetterNode(ZGetterNode Node);
	public abstract void VisitSetterNode(ZSetterNode Node);
	public abstract void VisitGetIndexNode(ZGetIndexNode Node);
	public abstract void VisitSetIndexNode(ZSetIndexNode Node);
	public abstract void VisitMethodCallNode(ZMethodCallNode Node);
	public abstract void VisitFuncCallNode(ZFuncCallNode Node);
	public abstract void VisitMacroNode(ZMacroNode FuncNode);
	public abstract void VisitUnaryNode(ZUnaryNode Node);
	public abstract void VisitNotNode(ZNotNode Node);
	public abstract void VisitCastNode(ZCastNode Node);
	public abstract void VisitInstanceOfNode(ZInstanceOfNode Node);
	public abstract void VisitBinaryNode(ZBinaryNode Node);
	public abstract void VisitComparatorNode(ZComparatorNode Node);
	public abstract void VisitAndNode(ZAndNode Node);
	public abstract void VisitOrNode(ZOrNode Node);
	public abstract void VisitBlockNode(ZBlockNode Node);
	public abstract void VisitVarNode(ZVarNode Node);
	public abstract void VisitIfNode(ZIfNode Node);
	public abstract void VisitReturnNode(ZReturnNode Node);
	public abstract void VisitWhileNode(ZWhileNode Node);
	public abstract void VisitBreakNode(ZBreakNode Node);
	public abstract void VisitThrowNode(ZThrowNode Node);
	public abstract void VisitTryNode(ZTryNode Node);
	public abstract void VisitLetNode(ZLetNode Node);
	public abstract void VisitFunctionNode(ZFunctionNode Node);
	public abstract void VisitClassNode(ZClassNode Node);
	public abstract void VisitAsmNode(ZAsmNode Node);
	public abstract void VisitErrorNode(ZErrorNode Node);
	public abstract void VisitTopLevelNode(ZTopLevelNode Node);
	public abstract void VisitSugarNode(ZSugarNode Node);
	public abstract void VisitLocalDefinedNode(ZLocalDefinedNode Node);

	public abstract void EnableVisitor();
	public abstract void StopVisitor();
	public abstract boolean IsVisitable();
}