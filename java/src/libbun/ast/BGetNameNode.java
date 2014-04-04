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

import libbun.parser.BGenerator;
import libbun.parser.BToken;
import libbun.parser.BVisitor;
import libbun.type.BType;
import libbun.util.BField;
import libbun.util.Nullable;

public class BGetNameNode extends BNode {
	@BField public boolean IsCaptured = false;
	@BField public String  GivenName;
	@BField public int     VarIndex = 0;
	@BField @Nullable public BLetVarNode ResolvedNode = null;

	public BGetNameNode(BNode ParentNode, BToken SourceToken, String GivenName) {
		super(ParentNode, SourceToken, 0);
		this.GivenName = GivenName;
		this.Type = BType.VoidType; // FIXME
	}

	//	public final boolean IsGlobalName() {
	//		if(this.ResolvedNode != null) {
	//			return this.ResolvedNode.GetDefiningFunctionNode() == null;
	//		}
	//		return false;
	//	}
	//
	//	public final String GetName() {
	//		@Var BNode ResolvedNode = this.ResolvedNode;
	//		if(ResolvedNode != null) {
	//			@Var ZFunctionNode DefNode = this.ResolvedNode.GetDefiningFunctionNode();
	//			if(DefNode == null) {
	//				if(ResolvedNode instanceof BLetVarNode) {
	//					return ((BLetVarNode)ResolvedNode).UniqueName;
	//				}
	//			}
	//		}
	//		return this.GivenName;
	//	}

	public final String GetUniqueName(BGenerator Generator) {
		if(this.ResolvedNode != null) {
			return this.ResolvedNode.GetUniqueName(Generator);
		}
		return this.GivenName;
	}


	@Override public void Accept(BVisitor Visitor) {
		Visitor.VisitGetNameNode(this);
	}

}