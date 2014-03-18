// ***************************************************************************
// Copyright (c) 2013, JST/CREST DEOS project authors. All rights reserved.
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

//ifdef  JAVA
package libbun.encode;
import libbun.parser.ZToken;
import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.ZBreakNode;
import libbun.parser.ast.ZClassNode;
import libbun.parser.ast.ZFunctionNode;
import libbun.parser.ast.ZGetNameNode;
import libbun.parser.ast.ZGetterNode;
import libbun.parser.ast.ZLetVarNode;
import libbun.parser.ast.ZNode;
import libbun.parser.ast.ZSetNameNode;
import libbun.parser.ast.ZSetterNode;
import libbun.type.ZClassField;
import libbun.type.ZClassType;
import libbun.type.ZType;
import libbun.util.LibZen;
import libbun.util.Var;


//Zen Generator should be written in each language.

public class PerlGenerator extends ZSourceGenerator {

	public PerlGenerator() {
		super("pl", "Perl-5.0 or later");
		this.IsDynamicLanguage = true;
		this.TrueLiteral  = "1";
		this.FalseLiteral = "0";
		this.NullLiteral = "undef";
		this.LineComment = "##";

	}

	@Override protected String GetBinaryOperator(ZType Type, ZToken Token) {
		if(Type.IsStringType()) {
			if(Token.EqualsText('+')) {
				return ".";
			}
		}
		return Token.GetText();
	}


	//	@Override public void VisitArrayLiteralNode(ZArrayLiteralNode Node) {
	//		this.CurrentBuilder.Append("{");
	//		for (int i = 0; i < LibZ.ListSize(Node.NodeList); i++) {
	//			if(i != 0) {
	//				this.CurrentBuilder.Append(", ");
	//			}
	//			Node.NodeList.get(i).Accept(this);
	//		}
	//		this.CurrentBuilder.Append("}");
	//	}

	//	@Override public void VisitMapLiteralNode(ZMapLiteralNode Node) {
	//		this.VisitingBuilder.Append("");
	//	}

	//	@Override public void VisitFunctionNode(ZFunctionNode Node) {
	//		this.VisitingBuilder.Append("");
	//	}

	private String VariablePrefix(ZType Type) {
		if(Type.IsArrayType()) {
			return "@";
		}
		if(Type.IsMapType() || Type instanceof ZClassType) {
			return "%";
		}
		return "$";
	}

	@Override public void VisitGetNameNode(ZGetNameNode Node) {
		this.CurrentBuilder.Append(this.VariablePrefix(Node.Type), this.NameLocalVariable(Node.GetNameSpace(), Node.GetName()));
	}

	@Override public void VisitSetNameNode(ZSetNameNode Node) {
		this.CurrentBuilder.Append(this.VariablePrefix(Node.GetAstType(ZSetNameNode._Expr)), this.NameLocalVariable(Node.GetNameSpace(), Node.GetName()), " = ");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override public void VisitGetterNode(ZGetterNode Node) {
		this.GenerateCode(null, Node.RecvNode());
		this.CurrentBuilder.Append("->{\'", Node.GetName(), "\'} = ");
	}

	@Override public void VisitSetterNode(ZSetterNode Node) {
		this.GenerateCode(null, Node.RecvNode());
		this.CurrentBuilder.Append("->{\'", Node.GetName(), "\'}");
		this.GenerateCode(null, Node.ExprNode());
	}

	@Override
	protected void VisitVarDeclNode(ZLetVarNode Node) {
		this.CurrentBuilder.Append("my ", this.VariablePrefix(Node.DeclType().GetRealType()));
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetName()), " = ");
		this.GenerateCode(null, Node.InitValueNode());
		this.CurrentBuilder.Append(this.SemiColon);
		if(Node.HasNextVarNode()) { this.VisitVarDeclNode(Node.NextVarNode()); }
	}

	@Override public void VisitLetNode(ZLetVarNode Node) {
		this.CurrentBuilder.Append(this.VariablePrefix(Node.DeclType().GetRealType()), Node.GlobalName, " = ");
		this.GenerateCode(null, Node.InitValueNode());
	}

	//	@Override public void VisitIfNode(ZIfNode Node) {
	//		this.CurrentBuilder.Append("if(");
	//		Node.AST[ZIfNode.Cond].Accept(this);
	//		this.CurrentBuilder.Append(")");
	//		this.VisitIndentBlock("{", Node.AST[ZIfNode.Then], "}");
	//		if(Node.AST[ZIfNode.Else] != null) {
	//			this.CurrentBuilder.Append("else");
	//			this.VisitIndentBlock("{", Node.AST[ZIfNode.Else], "}");
	//		}
	//	}
	//
	//	@Override public void VisitWhileNode(ZWhileNode Node) {
	//		this.CurrentBuilder.Append("while(");
	//		Node.AST[ZIfNode.Cond].Accept(this);
	//		this.CurrentBuilder.Append(")");
	//		this.VisitIndentBlock("{", Node.BodyNode, "}");
	//	}


	@Override public void VisitBreakNode(ZBreakNode Node) {
		this.CurrentBuilder.Append("last");
	}

	@Override protected void VisitParamNode(ZLetVarNode Node) {
		this.CurrentBuilder.Append("my ", this.VariablePrefix(Node.Type));
		this.CurrentBuilder.Append(this.NameLocalVariable(Node.GetNameSpace(), Node.GetName()), " = shift");
	}

	@Override public void VisitFunctionNode(ZFunctionNode Node) {
		this.CurrentBuilder.Append("sub");
		if(Node.FuncName() != null) {
			this.CurrentBuilder.AppendWhiteSpace();
			this.CurrentBuilder.Append(Node.FuncName());
		}
		this.CurrentBuilder.Append(" {");
		this.CurrentBuilder.Indent();
		//		if(Node.HasNextVarNode()) { this.VisitVarDeclNode(Node.NextVarNode()); }
		this.CurrentBuilder.Append(this.SemiColon);
		@Var ZNode BlockNode = Node.BlockNode();
		if(BlockNode instanceof ZBlockNode) {
			this.VisitStmtList((ZBlockNode)BlockNode);
		}
		this.CurrentBuilder.Append(this.SemiColon);
		this.CurrentBuilder.UnIndent();
		this.CurrentBuilder.AppendNewLine();
		this.CurrentBuilder.Append("}");
		this.CurrentBuilder.AppendNewLine();
		this.CurrentBuilder.AppendNewLine();
	}

	//	private void GenerateCField(String CType, String FieldName) {
	//		this.CurrentBuilder.AppendLineFeed();
	//		this.CurrentBuilder.AppendIndent();
	//		this.CurrentBuilder.Append(CType);
	//		this.CurrentBuilder.AppendWhiteSpace();
	//		this.CurrentBuilder.Append(FieldName);
	//		this.CurrentBuilder.Append(this.SemiColon);
	//	}
	//
	//	private void GenerateField(ZType DeclType, String FieldName) {
	//		this.CurrentBuilder.AppendNewLine();
	//		this.GenerateTypeName(DeclType);
	//		this.CurrentBuilder.AppendWhiteSpace();
	//		this.CurrentBuilder.Append(FieldName);
	//		this.CurrentBuilder.Append(this.SemiColon);
	//	}


	private String ClassKey(ZType ClassType) {
		return LibZen._QuoteString(this.NameClass(ClassType));
	}

	@Override public void VisitClassNode(ZClassNode Node) {
		this.CurrentBuilder.Append("sub _Init", this.NameClass(Node.ClassType), "{");
		this.CurrentBuilder.Indent();
		this.CurrentBuilder.AppendNewLine();
		this.CurrentBuilder.Append("%o = shift", this.SemiColon);

		@Var ZType SuperType = Node.ClassType.GetSuperType();
		if(!SuperType.Equals(ZClassType._ObjectType)) {
			this.CurrentBuilder.AppendNewLine();
			this.CurrentBuilder.Append("_Init" + this.NameClass(SuperType) + "(%o);");
		}
		this.CurrentBuilder.AppendNewLine();
		this.CurrentBuilder.Append("$o{", this.ClassKey(Node.ClassType), "} = ");
		this.CurrentBuilder.AppendInt(Node.ClassType.TypeId);
		this.CurrentBuilder.Append(this.SemiColon);
		@Var int i = 0;
		while (i < Node.GetListSize()) {
			@Var ZLetVarNode FieldNode = Node.GetFieldNode(i);
			this.CurrentBuilder.AppendNewLine();
			this.CurrentBuilder.Append("$o{", LibZen._QuoteString(FieldNode.GetName()), "} = ");
			this.GenerateCode(null, FieldNode.InitValueNode());
			this.CurrentBuilder.Append(this.SemiColon);
			i = i + 1;
		}

		i = 0;
		while (i < Node.ClassType.GetFieldSize()) {
			@Var ZClassField ClassField = Node.ClassType.GetFieldAt(i);
			if(ClassField.FieldType.IsFuncType()) {
				this.CurrentBuilder.AppendNewLine();
				this.CurrentBuilder.Append("if (defined $", this.NameMethod(Node.ClassType, ClassField.FieldName), ") {");
				this.CurrentBuilder.Indent();
				this.CurrentBuilder.AppendNewLine();
				this.CurrentBuilder.Append("$o{", LibZen._QuoteString(ClassField.FieldName), "} = $");
				this.CurrentBuilder.Append(this.NameMethod(Node.ClassType, ClassField.FieldName), this.SemiColon);
				this.CurrentBuilder.UnIndent();
				this.CurrentBuilder.AppendNewLine();
				this.CurrentBuilder.Append("}");
			}
			i = i + 1;
		}
		this.CurrentBuilder.AppendNewLine();
		this.CurrentBuilder.UnIndent();
		this.CurrentBuilder.AppendNewLine();
		this.CurrentBuilder.Append("}");
		this.CurrentBuilder.AppendLineFeed();
		this.CurrentBuilder.AppendLineFeed();

		this.CurrentBuilder.Append("sub _New", this.NameClass(Node.ClassType), " {");
		this.CurrentBuilder.Indent();
		this.CurrentBuilder.AppendNewLine();
		this.CurrentBuilder.Append("%o = {}", this.SemiColon);
		this.CurrentBuilder.AppendNewLine();
		this.CurrentBuilder.Append("_Init" + this.NameClass(Node.ClassType) + "(%o);");
		this.CurrentBuilder.AppendNewLine();
		this.CurrentBuilder.Append("return %o;");
		this.CurrentBuilder.UnIndent();
		this.CurrentBuilder.AppendNewLine();
		this.CurrentBuilder.Append("}");
		this.CurrentBuilder.AppendLineFeed();
		this.CurrentBuilder.AppendLineFeed();
	}
}