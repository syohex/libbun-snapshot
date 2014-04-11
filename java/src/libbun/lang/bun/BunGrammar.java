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


package libbun.lang.bun;
import libbun.ast.BNode;
import libbun.ast.BunBlockNode;
import libbun.ast.EmptyNode;
import libbun.ast.GroupNode;
import libbun.ast.binary.BInstanceOfNode;
import libbun.ast.binary.BinaryOperatorNode;
import libbun.ast.binary.BunAddNode;
import libbun.ast.binary.BunAndNode;
import libbun.ast.binary.BunBitwiseAndNode;
import libbun.ast.binary.BunBitwiseOrNode;
import libbun.ast.binary.BunBitwiseXorNode;
import libbun.ast.binary.BunDivNode;
import libbun.ast.binary.BunEqualsNode;
import libbun.ast.binary.BunGreaterThanEqualsNode;
import libbun.ast.binary.BunGreaterThanNode;
import libbun.ast.binary.BunLeftShiftNode;
import libbun.ast.binary.BunLessThanEqualsNode;
import libbun.ast.binary.BunLessThanNode;
import libbun.ast.binary.BunModNode;
import libbun.ast.binary.BunMulNode;
import libbun.ast.binary.BunNotEqualsNode;
import libbun.ast.binary.BunOrNode;
import libbun.ast.binary.BunRightShiftNode;
import libbun.ast.binary.BunSubNode;
import libbun.ast.decl.BunClassNode;
import libbun.ast.decl.BunDefineNode;
import libbun.ast.decl.BunFunctionNode;
import libbun.ast.decl.BunLetVarNode;
import libbun.ast.decl.BunPrototypeNode;
import libbun.ast.decl.BunRequireNode;
import libbun.ast.error.ErrorNode;
import libbun.ast.expression.FuncCallNode;
import libbun.ast.expression.GetFieldNode;
import libbun.ast.expression.GetIndexNode;
import libbun.ast.expression.GetNameNode;
import libbun.ast.expression.MethodCallNode;
import libbun.ast.expression.NewObjectNode;
import libbun.ast.expression.SetFieldNode;
import libbun.ast.expression.SetIndexNode;
import libbun.ast.expression.SetNameNode;
import libbun.ast.literal.BunArrayLiteralNode;
import libbun.ast.literal.BunAsmNode;
import libbun.ast.literal.BunBooleanNode;
import libbun.ast.literal.BunFloatNode;
import libbun.ast.literal.BunIntNode;
import libbun.ast.literal.BunMapEntryNode;
import libbun.ast.literal.BunMapLiteralNode;
import libbun.ast.literal.BunNullNode;
import libbun.ast.literal.BunStringNode;
import libbun.ast.literal.BunTypeNode;
import libbun.ast.statement.BunBreakNode;
import libbun.ast.statement.BunIfNode;
import libbun.ast.statement.BunReturnNode;
import libbun.ast.statement.BunThrowNode;
import libbun.ast.statement.BunTryNode;
import libbun.ast.statement.BunWhileNode;
import libbun.ast.sugar.BunAssertNode;
import libbun.ast.unary.BunCastNode;
import libbun.ast.unary.BunComplementNode;
import libbun.ast.unary.BunMinusNode;
import libbun.ast.unary.BunNotNode;
import libbun.ast.unary.BunPlusNode;
import libbun.ast.unary.UnaryOperatorNode;
import libbun.parser.BNameSpace;
import libbun.parser.BPatternToken;
import libbun.parser.BSourceContext;
import libbun.parser.BSyntax;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.parser.BTypeChecker;
import libbun.type.BClassType;
import libbun.type.BFuncType;
import libbun.type.BGenericType;
import libbun.type.BType;
import libbun.type.BTypePool;
import libbun.util.BArray;
import libbun.util.BLib;
import libbun.util.BMatchFunction;
import libbun.util.BTokenFunction;
import libbun.util.Var;

// Token

class WhiteSpaceTokenFunction extends BTokenFunction {
	@Override public boolean Invoke(BSourceContext SourceContext) {
		SourceContext.SkipWhiteSpace();
		return true;
	}
}

class NewLineTokenFunction extends BTokenFunction {
	@Override public boolean Invoke(BSourceContext SourceContext) {
		@Var int StartIndex = SourceContext.GetPosition() + 1;
		SourceContext.MoveNext();
		SourceContext.SkipWhiteSpace();
		SourceContext.FoundIndent(StartIndex, SourceContext.GetPosition());
		return true;
	}
}

class BlockCommentFunction extends BTokenFunction {
	@Override public boolean Invoke(BSourceContext SourceContext) {
		@Var int StartIndex = SourceContext.GetPosition();
		@Var char NextChar = SourceContext.GetCharAtFromCurrentPosition(+1);
		if(NextChar != '/' && NextChar != '*') {
			return false;  // another tokenizer
		}
		if(NextChar == '/') { // SingleLineComment
			while(SourceContext.HasChar()) {
				@Var char ch = SourceContext.GetCurrentChar();
				if(ch == '\n') {
					break;
				}
				SourceContext.MoveNext();
			}
			return true;
		}
		@Var int NestedLevel = 0;
		@Var char PrevChar = '\0';
		while(SourceContext.HasChar()) {
			NextChar = SourceContext.GetCurrentChar();
			//System.out.println("P,N"+PrevChar+","+NextChar);
			if(PrevChar == '*' && NextChar == '/') {
				NestedLevel = NestedLevel - 1;
				if(NestedLevel == 0) {
					SourceContext.MoveNext();
					return true;
				}
			}
			if(PrevChar == '/' && NextChar == '*') {
				NestedLevel = NestedLevel + 1;
			}
			SourceContext.MoveNext();
			PrevChar = NextChar;
		}
		SourceContext.LogWarning(StartIndex, "unfound */");
		return true;
	}
}

class CLineComment extends BTokenFunction {
	@Override public boolean Invoke(BSourceContext SourceContext) {
		// TODO Auto-generated method stub
		return false;
	}
}

class NameTokenFunction extends BTokenFunction {
	@Override public boolean Invoke(BSourceContext SourceContext) {
		@Var int StartIndex = SourceContext.GetPosition();
		while(SourceContext.HasChar()) {
			@Var char ch = SourceContext.GetCurrentChar();
			if(!BLib._IsSymbol(ch) && !BLib._IsDigit(ch)) {
				break;
			}
			SourceContext.MoveNext();
		}
		SourceContext.Tokenize(StartIndex, SourceContext.GetPosition());
		return true;
	}

}

class OperatorTokenFunction extends BTokenFunction {
	@Override public boolean Invoke(BSourceContext SourceContext) {
		SourceContext.TokenizeDefinedSymbol(SourceContext.GetPosition());
		return true;
	}
}

class NumberLiteralTokenFunction extends BTokenFunction {

	public final static char _ParseDigit(BSourceContext SourceContext) {
		@Var char ch = '\0';
		while(SourceContext.HasChar()) {
			ch = SourceContext.GetCurrentChar();
			if(!BLib._IsDigit(ch)) {
				break;
			}
			SourceContext.MoveNext();
		}
		return ch;
	}

	@Override public boolean Invoke(BSourceContext SourceContext) {
		@Var int StartIndex = SourceContext.GetPosition();
		@Var char ch = NumberLiteralTokenFunction._ParseDigit(SourceContext);
		if(ch == '.') {
			SourceContext.MoveNext();
			ch = NumberLiteralTokenFunction._ParseDigit(SourceContext);
			if(ch == 'e' || ch == 'E') {
				SourceContext.MoveNext();
				if(SourceContext.HasChar()) {
					ch = SourceContext.GetCurrentChar();
					if(ch == '+' || ch == '-') {
						SourceContext.MoveNext();
					}
				}
				if(SourceContext.HasChar() && !BLib._IsDigit(SourceContext.GetCurrentChar())) {
					SourceContext.LogWarning(StartIndex, "exponent has no digits");
				}
				NumberLiteralTokenFunction._ParseDigit(SourceContext);
			}
			SourceContext.Tokenize("$FloatLiteral$", StartIndex, SourceContext.GetPosition());
		}
		else {
			SourceContext.Tokenize("$IntegerLiteral$", StartIndex, SourceContext.GetPosition());
		}
		return true;
	}
}

class StringLiteralTokenFunction extends BTokenFunction {
	@Override public boolean Invoke(BSourceContext SourceContext) {
		@Var int StartIndex = SourceContext.GetPosition();
		SourceContext.MoveNext();
		while(SourceContext.HasChar()) {
			@Var char ch = SourceContext.GetCurrentChar();
			if(ch == '\"') {
				SourceContext.MoveNext(); // eat '"'
				SourceContext.Tokenize("$StringLiteral$", StartIndex, SourceContext.GetPosition());
				return true;
			}
			if(ch == '\n') {
				break;
			}
			if(ch == '\\') {
				SourceContext.MoveNext();
			}
			SourceContext.MoveNext();
		}
		SourceContext.LogWarning(StartIndex, "unclosed \"");
		SourceContext.Tokenize("$StringLiteral$", StartIndex, SourceContext.GetPosition());
		return false;
	}
}


// Syntax Pattern

class NullPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		return new BunNullNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext));
	}
}

class TruePatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		return new BunBooleanNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), true);
	}
}

class FalsePatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		return new BunBooleanNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), false);
	}
}

class BunNotPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode UnaryNode = new BunNotNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext));
		UnaryNode = TokenContext.MatchPattern(UnaryNode, UnaryOperatorNode._Recv, "$RightExpression$", BTokenContext._Required);
		return UnaryNode;
	}
}

class BunPlusPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode UnaryNode = new BunPlusNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext));
		return TokenContext.MatchPattern(UnaryNode, UnaryOperatorNode._Recv, "$RightExpression$", BTokenContext._Required);
	}
}
class BunMinusPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode UnaryNode = new BunMinusNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext));
		return TokenContext.MatchPattern(UnaryNode, UnaryOperatorNode._Recv, "$RightExpression$", BTokenContext._Required);
	}
}
class BunComplementPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode UnaryNode = new BunComplementNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext));
		return TokenContext.MatchPattern(UnaryNode, UnaryOperatorNode._Recv, "$RightExpression$", BTokenContext._Required);
	}
}

class BunAndPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunAndNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunOrPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunOrNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunAddPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunAddNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunSubPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunSubNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunMulPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunMulNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunDivPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunDivNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunModPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunModNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunBitwiseAndPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunBitwiseAndNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunBitwiseOrPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunBitwiseOrNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunBitwiseXorPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunBitwiseXorNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunLeftShiftPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunLeftShiftNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunRightShiftPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunRightShiftNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunEqualsPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunEqualsNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunNotEqualsPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunNotEqualsNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunLessThanPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunLessThanNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunLessThanEqualsPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunLessThanEqualsNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunGreaterThanPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunGreaterThanNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunGreaterThanEqualsPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BinaryOperatorNode BinaryNode = new BunGreaterThanEqualsNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class StringLiteralPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		return new BunStringNode(ParentNode, Token, BLib._UnquoteString(Token.GetText()));
	}
}

class IntLiteralPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		return new BunIntNode(ParentNode, Token, BLib._ParseInt(Token.GetText()));
	}
}

class FloatLiteralPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		return new BunFloatNode(ParentNode, Token, BLib._ParseFloat(Token.GetText()));
	}
}

class DefinedTypePatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var BType Type = ParentNode.GetNameSpace().GetType(Token.GetText(), Token, false/*IsCreation*/);
		if(Type != null) {
			@Var BunTypeNode TypeNode = new BunTypeNode(ParentNode, Token, Type);
			return TokenContext.ParsePatternAfter(ParentNode, TypeNode, "$TypeRight$", BTokenContext._Optional);
		}
		return null;
	}
}

class OpenTypePatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken MaybeToken   = null;
		@Var BToken MutableToken = null;
		if(TokenContext.IsToken("maybe")) {
			MaybeToken   = TokenContext.GetToken(BTokenContext._MoveNext);
		}
		if(TokenContext.MatchToken("mutable")) {
			MutableToken   = TokenContext.GetToken(BTokenContext._MoveNext);
		}
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var BType Type = ParentNode.GetNameSpace().GetType(Token.GetText(), Token, true/*IsCreation*/);
		if(Type != null) {
			@Var BunTypeNode TypeNode = new BunTypeNode(ParentNode, Token, Type);
			@Var BNode Node = TokenContext.ParsePatternAfter(ParentNode, TypeNode, "$TypeRight$", BTokenContext._Optional);
			if(Node instanceof BunTypeNode) {
				@Var BTypeChecker Gamma = ParentNode.GetNameSpace().Generator.TypeChecker;
				if(MutableToken != null) {
					Node.Type = BTypePool._LookupMutableType(Gamma, Node.Type, MutableToken);
				}
				if(MaybeToken != null) {
					Node.Type = BTypePool._LookupNullableType(Gamma, Node.Type, MaybeToken);
				}
			}
			return Node;
		}
		return null; // Not Matched
	}
}

class RightTypePatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftTypeNode) {
		@Var BToken SourceToken = TokenContext.GetToken();
		if(LeftTypeNode.Type.GetParamSize() > 0) {
			if(TokenContext.MatchToken("<")) {  // Generics
				@Var BArray<BType> TypeList = new BArray<BType>(new BType[4]);
				while(!TokenContext.StartsWithToken(">")) {
					if(TypeList.size() > 0 && !TokenContext.MatchToken(",")) {
						return null;
					}
					@Var BunTypeNode ParamTypeNode = (BunTypeNode) TokenContext.ParsePattern(ParentNode, "$OpenType$", BTokenContext._Optional);
					if(ParamTypeNode == null) {
						return LeftTypeNode;
					}
					TypeList.add(ParamTypeNode.Type);
				}
				LeftTypeNode = new BunTypeNode(ParentNode, SourceToken, BTypePool._GetGenericType(LeftTypeNode.Type, TypeList, true));
			}
		}
		while(TokenContext.MatchToken("[")) {  // Array
			if(!TokenContext.MatchToken("]")) {
				return null;
			}
			LeftTypeNode = new BunTypeNode(ParentNode, SourceToken, BTypePool._GetGenericType1(BGenericType._ArrayType, LeftTypeNode.Type));
		}
		return LeftTypeNode;
	}
}

class TypeAnnotationPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		if(TokenContext.MatchToken(":")) {
			return TokenContext.ParsePattern(ParentNode, "$OpenType$", BTokenContext._Required);
		}
		return null;
	}
}

class GetFieldPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode GetterNode = new GetFieldNode(ParentNode, LeftNode);
		GetterNode = TokenContext.MatchToken(GetterNode, ".", BTokenContext._Required);
		GetterNode = TokenContext.MatchPattern(GetterNode, GetFieldNode._NameInfo, "$Name$", BTokenContext._Required);
		return GetterNode;
	}
}

class SetFieldPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode SetterNode = new SetFieldNode(ParentNode, LeftNode);
		SetterNode = TokenContext.MatchToken(SetterNode, ".", BTokenContext._Required);
		SetterNode = TokenContext.MatchPattern(SetterNode, SetFieldNode._NameInfo, "$Name$", BTokenContext._Required);
		SetterNode = TokenContext.MatchToken(SetterNode, "=", BTokenContext._Required);
		SetterNode = TokenContext.MatchPattern(SetterNode, SetFieldNode._Expr, "$Expression$", BTokenContext._Required);
		return SetterNode;
	}
}

class MethodCallPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode Node = new MethodCallNode(ParentNode, LeftNode);
		Node = TokenContext.MatchToken(Node, ".", BTokenContext._Required);
		Node = TokenContext.MatchPattern(Node, MethodCallNode._NameInfo, "$Name$", BTokenContext._Required);
		Node = TokenContext.MatchNtimes(Node, "(", "$Expression$", ",", ")");
		return Node;
	}
}

class GroupPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode Node = new GroupNode(ParentNode);
		Node = TokenContext.MatchToken(Node, "(", BTokenContext._Required);
		Node = TokenContext.MatchPattern(Node, GroupNode._Expr, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		Node = TokenContext.MatchToken(Node, ")", BTokenContext._Required);
		return Node;
	}
}

class CastPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode CastNode = new BunCastNode(ParentNode, BType.VarType, null);
		CastNode = TokenContext.MatchToken(CastNode, "(", BTokenContext._Required);
		CastNode = TokenContext.MatchPattern(CastNode, BunCastNode._TypeInfo, "$Type$", BTokenContext._Required);
		CastNode = TokenContext.MatchToken(CastNode, ")", BTokenContext._Required);
		CastNode = TokenContext.MatchPattern(CastNode, BunCastNode._Expr, "$RightExpression$", BTokenContext._Required);
		if(CastNode instanceof BunCastNode) {
			((BunCastNode)CastNode).CastType();  // due to old implementation that cannot be fixed easily.
		}
		return CastNode;
	}
}

class FuncCallPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ApplyNode = new FuncCallNode(ParentNode, LeftNode);
		ApplyNode = TokenContext.MatchNtimes(ApplyNode, "(", "$Expression$", ",", ")");
		return ApplyNode;
	}
}

class GetIndexPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode IndexerNode = new GetIndexNode(ParentNode, LeftNode);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "[", BTokenContext._Required);
		IndexerNode = TokenContext.MatchPattern(IndexerNode, GetIndexNode._Index, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "]", BTokenContext._Required);
		return IndexerNode;
	}
}

class SetIndexPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode IndexerNode = new SetIndexNode(ParentNode, LeftNode);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "[", BTokenContext._Required);
		IndexerNode = TokenContext.MatchPattern(IndexerNode, SetIndexNode._Index, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "]", BTokenContext._Required);
		IndexerNode = TokenContext.MatchToken(IndexerNode, "=", BTokenContext._Required);
		IndexerNode = TokenContext.MatchPattern(IndexerNode, SetIndexNode._Expr, "$Expression$", BTokenContext._Required);
		return IndexerNode;
	}
}

class ArrayLiteralPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LiteralNode = new BunArrayLiteralNode(ParentNode);
		LiteralNode = TokenContext.MatchNtimes(LiteralNode, "[", "$Expression$", ",", "]");
		return LiteralNode;
	}
}

class NewObjectPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LiteralNode = new NewObjectNode(ParentNode);
		LiteralNode = TokenContext.MatchToken(LiteralNode, "new", BTokenContext._Required);
		LiteralNode = TokenContext.MatchPattern(LiteralNode, NewObjectNode._TypeInfo, "$OpenType$", BTokenContext._Optional);
		LiteralNode = TokenContext.MatchNtimes(LiteralNode, "(", "$Expression$", ",", ")");
		return LiteralNode;
	}
}

class MapEntryPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LiteralNode = new BunMapEntryNode(ParentNode);
		LiteralNode = TokenContext.MatchPattern(LiteralNode, BunMapEntryNode._Key, "$Expression$", BTokenContext._Required);
		LiteralNode = TokenContext.MatchToken(LiteralNode, ":", BTokenContext._Required);
		LiteralNode = TokenContext.MatchPattern(LiteralNode, BunMapEntryNode._Value, "$Expression$", BTokenContext._Required);
		return LiteralNode;
	}
}

class MapLiteralPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LiteralNode = new BunMapLiteralNode(ParentNode);
		LiteralNode = TokenContext.MatchNtimes(LiteralNode, "{", "$MapEntry$", ",", "}");
		return LiteralNode;
	}
}

class StatementEndPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var boolean ContextAllowance = TokenContext.SetParseFlag(false);
		@Var BToken Token = null;
		if(TokenContext.HasNext()) {
			Token = TokenContext.GetToken();
			if(!Token.EqualsText(';') && !Token.IsIndent()) {
				TokenContext.SetParseFlag(ContextAllowance);
				return TokenContext.CreateExpectedErrorNode(Token, ";");
			}
			TokenContext.MoveNext();
			while(TokenContext.HasNext()) {
				Token = TokenContext.GetToken();
				if(!Token.EqualsText(';') && !Token.IsIndent()) {
					break;
				}
				TokenContext.MoveNext();
			}
		}
		TokenContext.SetParseFlag(ContextAllowance);
		return new EmptyNode(ParentNode, Token);
	}
}

class BlockPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode BlockNode = new BunBlockNode(ParentNode, null);
		@Var BToken SkipToken = TokenContext.GetToken();
		BlockNode = TokenContext.MatchToken(BlockNode, "{", BTokenContext._Required);
		if(!BlockNode.IsErrorNode()) {
			@Var boolean Remembered = TokenContext.SetParseFlag(BTokenContext._AllowSkipIndent); // init
			while(TokenContext.HasNext()) {
				//System.out.println("Token :" + TokenContext.GetToken());
				if(TokenContext.MatchToken("}")) {
					break;
				}
				BlockNode = TokenContext.MatchPattern(BlockNode, BNode._AppendIndex, "$Statement$", BTokenContext._Required);
				if(BlockNode.IsErrorNode()) {
					TokenContext.SkipError(SkipToken);
					TokenContext.MatchToken("}");
					break;
				}
			}
			TokenContext.SetParseFlag(Remembered);
		}
		return BlockNode;
	}
}

class AnnotationPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		// TODO Auto-generated method stub
		return null;
	}
}

class SymbolExpressionPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken NameToken = TokenContext.GetToken(BTokenContext._MoveNext);
		if(TokenContext.IsToken("=")) {
			return new ErrorNode(ParentNode, TokenContext.GetToken(), "assignment is not en expression");
		}
		else {
			return new GetNameNode(ParentNode, NameToken, NameToken.GetText());
		}
	}
}

class SymbolStatementPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken NameToken = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var GetNameNode NameNode = new GetNameNode(ParentNode, NameToken, NameToken.GetText());
		if(TokenContext.IsToken("=")) {
			@Var BNode AssignedNode = new SetNameNode(ParentNode, null, NameNode);
			AssignedNode = TokenContext.MatchToken(AssignedNode, "=", BTokenContext._Required);
			AssignedNode = TokenContext.MatchPattern(AssignedNode, SetNameNode._Expr, "$Expression$", BTokenContext._Required);
			return AssignedNode;
		}
		else {
			return NameNode;
		}
	}
}

class StatementPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var boolean Remembered = TokenContext.SetParseFlag(BTokenContext._AllowSkipIndent);
		//		@Var ZAnnotationNode AnnotationNode = (ZAnnotationNode)TokenContext.ParsePattern(ParentNode, "$Annotation$", ZTokenContext.Optional2);
		TokenContext.SetParseFlag(BTokenContext._NotAllowSkipIndent);
		@Var BNode StmtNode = BunGrammar._DispatchPattern(ParentNode, TokenContext, null, true, true);
		StmtNode = TokenContext.MatchPattern(StmtNode, BNode._Nop, ";", BTokenContext._Required);
		//		if(AnnotationNode != null) {
		//			AnnotationNode.Append(StmtNode);
		//			StmtNode = AnnotationNode;
		//		}
		TokenContext.SetParseFlag(Remembered);
		return StmtNode;
	}
}

class ExpressionPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		return BunGrammar._DispatchPattern(ParentNode, TokenContext, LeftNode, false, true);
	}
}

class RightExpressionPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		return BunGrammar._DispatchPattern(ParentNode, TokenContext, LeftNode, false, false);
	}
}

class InStatementPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		TokenContext.SetParseFlag(BTokenContext._AllowSkipIndent);
		return BunGrammar._DispatchPattern(ParentNode, TokenContext, null, true, true);
	}
}

class IfPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode IfNode = new BunIfNode(ParentNode);
		IfNode = TokenContext.MatchToken(IfNode, "if", BTokenContext._Required);
		IfNode = TokenContext.MatchToken(IfNode, "(", BTokenContext._Required);
		IfNode = TokenContext.MatchPattern(IfNode, BunIfNode._Cond, "$Expression$", BTokenContext._Required, BTokenContext._AllowNewLine);
		IfNode = TokenContext.MatchToken(IfNode, ")", BTokenContext._Required);
		IfNode = TokenContext.MatchPattern(IfNode, BunIfNode._Then, "$Block$", BTokenContext._Required);
		if(TokenContext.MatchNewLineToken("else")) {
			if(TokenContext.IsNewLineToken("if")) {
				IfNode = TokenContext.MatchPattern(IfNode, BunIfNode._Else, "if", BTokenContext._Required);
			}
			else {
				IfNode = TokenContext.MatchPattern(IfNode, BunIfNode._Else, "$Block$", BTokenContext._Required);
			}
		}
		return IfNode;
	}
}

class WhilePatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode WhileNode = new BunWhileNode(ParentNode);
		WhileNode = TokenContext.MatchToken(WhileNode, "while", BTokenContext._Required);
		WhileNode = TokenContext.MatchToken(WhileNode, "(", BTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, BunWhileNode._Cond, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		if(TokenContext.MatchNewLineToken("whatever")) {
			WhileNode = TokenContext.MatchPattern(WhileNode, BunWhileNode._Next, "$InStatement$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		}
		WhileNode = TokenContext.MatchToken(WhileNode, ")", BTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, BunWhileNode._Block, "$Block$", BTokenContext._Required);
		return WhileNode;
	}
}

class BreakPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode BreakNode = new BunBreakNode(ParentNode);
		BreakNode = TokenContext.MatchToken(BreakNode, "break", BTokenContext._Required);
		return BreakNode;
	}
}

class ReturnPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ReturnNode = new BunReturnNode(ParentNode);
		ReturnNode = TokenContext.MatchToken(ReturnNode, "return", BTokenContext._Required);
		ReturnNode = TokenContext.MatchPattern(ReturnNode, BunReturnNode._Expr, "$Expression$", BTokenContext._Optional);
		return ReturnNode;
	}
}

class TryPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode TryNode = new BunTryNode(ParentNode);
		TryNode = TokenContext.MatchToken(TryNode, "try", BTokenContext._Required);
		TryNode = TokenContext.MatchPattern(TryNode, BunTryNode._Try, "$Block$", BTokenContext._Required);
		@Var int count = 0;
		if(TokenContext.MatchNewLineToken("catch")) {
			TryNode = TokenContext.MatchToken(TryNode, "(", BTokenContext._Required);
			TryNode = TokenContext.MatchPattern(TryNode, BunTryNode._NameInfo, "$Name$", BTokenContext._Required);
			TryNode = TokenContext.MatchToken(TryNode, ")", BTokenContext._Required);
			TryNode = TokenContext.MatchPattern(TryNode, BunTryNode._Catch, "$Block$", BTokenContext._Required);
			count = count + 1;
		}
		if(TokenContext.MatchNewLineToken("finally")) {
			TryNode = TokenContext.MatchPattern(TryNode, BunTryNode._Finally, "$Block$", BTokenContext._Required);
			count = count + 1;
		}
		if(count == 0 && !TryNode.IsErrorNode()) {
			TryNode = new ErrorNode(ParentNode, TryNode.SourceToken, "either catch or finally is expected");
		}
		return TryNode;
	}
}

class ThrowPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ThrowNode = new BunThrowNode(ParentNode);
		ThrowNode = TokenContext.MatchToken(ThrowNode, "throw", BTokenContext._Required);
		ThrowNode = TokenContext.MatchPattern(ThrowNode, BunThrowNode._Expr, "$Expression$", BTokenContext._Required);
		return ThrowNode;
	}
}

class NamePatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken Token = TokenContext.GetToken(BTokenContext._MoveNext);
		if(BLib._IsSymbol(Token.GetChar())) {
			return new GetNameNode(ParentNode, Token, Token.GetText());
		}
		return new ErrorNode(ParentNode, Token, "illegal name: \'" + Token.GetText() + "\'");
	}
}

class VarPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode VarNode = new BunLetVarNode(ParentNode, 0, null, null);
		VarNode = TokenContext.MatchToken(VarNode, "var", BTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, BunLetVarNode._NameInfo, "$Name$", BTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, BunLetVarNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		VarNode = TokenContext.MatchToken(VarNode, "=", BTokenContext._Required);
		VarNode = TokenContext.MatchPattern(VarNode, BunLetVarNode._InitValue, "$Expression$", BTokenContext._Required);
		//		if(VarNode instanceof BunLetVarNode) {
		//			return new BunVarBlockNode(ParentNode, (BunLetVarNode)VarNode);
		//		}
		return VarNode;
	}
}

class ParamPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ParamNode = new BunLetVarNode(ParentNode, BunLetVarNode._IsReadOnly, null, null);
		ParamNode = TokenContext.MatchPattern(ParamNode, BunLetVarNode._NameInfo, "$Name$", BTokenContext._Required);
		ParamNode = TokenContext.MatchPattern(ParamNode, BunLetVarNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		return ParamNode;
	}
}

class FunctionPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode FuncNode = new BunFunctionNode(ParentNode);
		FuncNode = TokenContext.MatchToken(FuncNode, "function", BTokenContext._Required);
		FuncNode = TokenContext.MatchPattern(FuncNode, BunFunctionNode._NameInfo, "$Name$", BTokenContext._Optional);
		FuncNode = TokenContext.MatchNtimes(FuncNode, "(", "$Param$", ",", ")");
		FuncNode = TokenContext.MatchPattern(FuncNode, BunFunctionNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		FuncNode = TokenContext.MatchPattern(FuncNode, BunFunctionNode._Block, "$Block$", BTokenContext._Required);
		return FuncNode;
	}
}

class PrototypePatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode FuncNode = new BunFunctionNode(ParentNode);
		FuncNode = TokenContext.MatchToken(FuncNode, "function", BTokenContext._Required);
		FuncNode = TokenContext.MatchPattern(FuncNode, BunFunctionNode._NameInfo, "$Name$", BTokenContext._Required);
		FuncNode = TokenContext.MatchNtimes(FuncNode, "(", "$Param$", ",", ")");
		FuncNode = TokenContext.MatchPattern(FuncNode, BunFunctionNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Required);
		if(FuncNode instanceof BunFunctionNode) {
			return new BunPrototypeNode((BunFunctionNode)FuncNode);
		}
		return FuncNode;
	}
}

class LetPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LetNode = new BunLetVarNode(ParentNode, BunLetVarNode._IsReadOnly, null, null);
		LetNode = TokenContext.MatchToken(LetNode, "let", BTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BunLetVarNode._NameInfo, "$Name$", BTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BunLetVarNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		LetNode = TokenContext.MatchToken(LetNode, "=", BTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BunLetVarNode._InitValue, "$Expression$", BTokenContext._Required);
		return LetNode;
	}
}

class ExportPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken NameToken = TokenContext.GetToken(BTokenContext._MoveNext);
		@Var BNode Node = TokenContext.ParsePattern(ParentNode, "function", BTokenContext._Optional);
		if(Node instanceof BunFunctionNode) {
			((BunFunctionNode)Node).IsExport = true;
			return Node;
		}
		Node = TokenContext.ParsePattern(ParentNode, "let", BTokenContext._Optional);
		if(Node instanceof BunLetVarNode) {
			((BunLetVarNode)Node).NameFlag = ((BunLetVarNode)Node).NameFlag | BunLetVarNode._IsExport;
			return Node;
		}
		Node = TokenContext.ParsePattern(ParentNode, "class", BTokenContext._Optional);
		if(Node instanceof BunClassNode) {
			((BunClassNode)Node).IsExport = true;
			return Node;
		}
		return new ErrorNode(ParentNode, NameToken, "export function, class, or let");
	}
}

class ImportPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken NameToken = TokenContext.GetToken(BTokenContext._MoveNext);
		return new ErrorNode(ParentNode, NameToken, "unsupported import");
	}
}

class ClassPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ClassNode = new BunClassNode(ParentNode);
		ClassNode = TokenContext.MatchToken(ClassNode, "class", BTokenContext._Required);
		ClassNode = TokenContext.MatchPattern(ClassNode, BunClassNode._NameInfo, "$Name$", BTokenContext._Required);
		if(TokenContext.MatchNewLineToken("extends")) {
			ClassNode = TokenContext.MatchPattern(ClassNode, BunClassNode._TypeInfo, "$OpenType$", BTokenContext._Required);
		}
		ClassNode = TokenContext.MatchNtimes(ClassNode, "{", "$FieldDecl$", null, "}");
		return ClassNode;
	}
}

class FieldPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var boolean Rememberd = TokenContext.SetParseFlag(false);
		@Var BNode FieldNode = new BunLetVarNode(ParentNode, 0, null, null);
		FieldNode = TokenContext.MatchToken(FieldNode, "var", BTokenContext._Required);
		FieldNode = TokenContext.MatchPattern(FieldNode, BunLetVarNode._NameInfo, "$Name$", BTokenContext._Required);
		FieldNode = TokenContext.MatchPattern(FieldNode, BunLetVarNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		if(TokenContext.MatchToken("=")) {
			FieldNode = TokenContext.MatchPattern(FieldNode, BunLetVarNode._InitValue, "$Expression$", BTokenContext._Required);
		}
		FieldNode = TokenContext.MatchPattern(FieldNode, BNode._Nop, ";", BTokenContext._Required);
		TokenContext.SetParseFlag(Rememberd);
		return FieldNode;
	}
}

class InstanceOfPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode BinaryNode = new BInstanceOfNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		BinaryNode = TokenContext.MatchPattern(BinaryNode, BInstanceOfNode._TypeInfo, "$OpenType$", BTokenContext._Required);
		return BinaryNode;
	}
}

class AssertPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode AssertNode = new BunAssertNode(ParentNode);
		AssertNode = TokenContext.MatchToken(AssertNode, "assert", BTokenContext._Required);
		AssertNode = TokenContext.MatchToken(AssertNode, "(", BTokenContext._Required);
		AssertNode = TokenContext.MatchPattern(AssertNode, BunThrowNode._Expr, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		AssertNode = TokenContext.MatchToken(AssertNode, ")", BTokenContext._Required);
		return AssertNode;
	}

}

class AsmPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode AsmNode = new BunAsmNode(ParentNode, null, null, null);
		AsmNode = TokenContext.MatchToken(AsmNode, "asm", BTokenContext._Required);
		AsmNode = TokenContext.MatchToken(AsmNode, "(", BTokenContext._Required);
		AsmNode = TokenContext.MatchPattern(AsmNode, BunAsmNode._Macro, "$StringLiteral$", BTokenContext._Required);
		AsmNode = TokenContext.MatchToken(AsmNode, ")", BTokenContext._Required);
		AsmNode = TokenContext.MatchPattern(AsmNode, BunAsmNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Optional);
		return AsmNode;
	}
}

class BunDefineNamePatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BToken NameToken = TokenContext.ParseLargeToken();
		//System.out.println("'"+ NameToken.GetText() + "'");
		return new GetNameNode(ParentNode, NameToken, NameToken.GetText());
	}
}

class BunDefinePatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode LetNode = new BunLetVarNode(ParentNode, BunLetVarNode._IsReadOnly, null, null);
		LetNode = TokenContext.MatchToken(LetNode, "define", BTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BunLetVarNode._NameInfo, "$DefineName$", BTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BunLetVarNode._InitValue, "$StringLiteral$", BTokenContext._Required);
		LetNode = TokenContext.MatchPattern(LetNode, BunLetVarNode._TypeInfo, "$TypeAnnotation$", BTokenContext._Required);
		if(LetNode instanceof BunLetVarNode) {
			return new BunDefineNode(ParentNode, (BunLetVarNode)LetNode);
		}
		return LetNode;
	}
}

class RequirePatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode RequireNode = new BunRequireNode(ParentNode);
		RequireNode = TokenContext.MatchToken(RequireNode, "require", BTokenContext._Required);
		RequireNode = TokenContext.MatchPattern(RequireNode, BunRequireNode._Path, "$StringLiteral$", BTokenContext._Required);
		return RequireNode;
	}

}

public class BunGrammar {
	public final static BTokenFunction WhiteSpaceToken = new WhiteSpaceTokenFunction();
	public final static BTokenFunction NewLineToken = new NewLineTokenFunction();
	public final static BTokenFunction BlockComment = new BlockCommentFunction();
	public final static BTokenFunction LineComment = new CLineComment();
	public final static BTokenFunction NameToken = new NameTokenFunction();
	public final static BTokenFunction OperatorToken = new OperatorTokenFunction();
	public final static BTokenFunction StringLiteralToken = new StringLiteralTokenFunction();
	public final static BTokenFunction NumberLiteralToken = new NumberLiteralTokenFunction();

	public final static BMatchFunction NullPattern = new NullPatternFunction();
	public final static BMatchFunction TruePattern = new TruePatternFunction();
	public final static BMatchFunction FalsePattern = new FalsePatternFunction();

	public final static BMatchFunction NotPattern = new BunNotPatternFunction();
	public final static BMatchFunction PlusPattern = new BunPlusPatternFunction();
	public final static BMatchFunction MinusPattern = new BunMinusPatternFunction();
	public final static BMatchFunction ComplementPattern = new BunComplementPatternFunction();

	public final static BMatchFunction EqualsPattern = new BunEqualsPatternFunction();
	public final static BMatchFunction NotEqualsPattern = new BunNotEqualsPatternFunction();
	public final static BMatchFunction LessThanPattern = new BunLessThanPatternFunction();
	public final static BMatchFunction LessThanEqualsPattern = new BunLessThanEqualsPatternFunction();
	public final static BMatchFunction GreaterThanPattern = new BunGreaterThanPatternFunction();
	public final static BMatchFunction GreaterThanEqualsPattern = new BunGreaterThanEqualsPatternFunction();

	public final static BMatchFunction AndPattern = new BunAndPatternFunction();
	public final static BMatchFunction OrPattern = new BunOrPatternFunction();

	public final static BMatchFunction AddPattern = new BunAddPatternFunction();
	public final static BMatchFunction SubPattern = new BunSubPatternFunction();
	public final static BMatchFunction MulPattern = new BunMulPatternFunction();
	public final static BMatchFunction DivPattern = new BunDivPatternFunction();
	public final static BMatchFunction ModPattern = new BunModPatternFunction();

	public final static BMatchFunction BitwiseAndPattern = new BunBitwiseAndPatternFunction();
	public final static BMatchFunction BitwiseOrPattern = new BunBitwiseOrPatternFunction();
	public final static BMatchFunction BitwiseXorPattern = new BunBitwiseXorPatternFunction();
	public final static BMatchFunction LeftShiftPattern = new BunLeftShiftPatternFunction();
	public final static BMatchFunction RightShiftPattern = new BunRightShiftPatternFunction();

	public final static BMatchFunction StringLiteralPattern = new StringLiteralPatternFunction();
	public final static BMatchFunction IntLiteralPattern = new IntLiteralPatternFunction();
	public final static BMatchFunction FloatLiteralPattern = new FloatLiteralPatternFunction();

	public final static BMatchFunction TypePattern = new DefinedTypePatternFunction();
	public final static BMatchFunction OpenTypePattern = new OpenTypePatternFunction();
	public final static BMatchFunction TypeSuffixPattern = new RightTypePatternFunction();
	public final static BMatchFunction TypeAnnotationPattern = new TypeAnnotationPatternFunction();

	public final static BMatchFunction GetFieldPattern = new GetFieldPatternFunction();
	public final static BMatchFunction SetFieldPattern = new SetFieldPatternFunction();
	public final static BMatchFunction MethodCallPattern = new MethodCallPatternFunction();

	public final static BMatchFunction GroupPattern = new GroupPatternFunction();
	public final static BMatchFunction CastPattern = new CastPatternFunction();
	public final static BMatchFunction FuncCallPattern = new FuncCallPatternFunction();

	public final static BMatchFunction GetIndexPattern = new GetIndexPatternFunction();
	public final static BMatchFunction SetIndexPattern = new SetIndexPatternFunction();
	public final static BMatchFunction ArrayLiteralPattern = new ArrayLiteralPatternFunction();
	public final static BMatchFunction MapEntryPattern = new MapEntryPatternFunction();
	public final static BMatchFunction MapLiteralPattern = new MapLiteralPatternFunction();
	public final static BMatchFunction NewObjectPattern = new NewObjectPatternFunction();

	public final static BMatchFunction StatementEndPattern = new StatementEndPatternFunction();
	public final static BMatchFunction BlockPattern = new BlockPatternFunction();
	public final static BMatchFunction AnnotationPattern = new AnnotationPatternFunction();
	public final static BMatchFunction SymbolExpressionPattern = new SymbolExpressionPatternFunction();
	public final static BMatchFunction SymbolStatementPattern = new SymbolStatementPatternFunction();
	public final static BMatchFunction StatementPattern = new StatementPatternFunction();
	public final static BMatchFunction ExpressionPattern = new ExpressionPatternFunction();
	public final static BMatchFunction RightExpressionPattern = new RightExpressionPatternFunction();
	public final static BMatchFunction InStatementPattern = new InStatementPatternFunction();

	public final static BMatchFunction IfPattern = new IfPatternFunction();
	public final static BMatchFunction WhilePattern = new WhilePatternFunction();
	public final static BMatchFunction BreakPattern = new BreakPatternFunction();
	public final static BMatchFunction ReturnPattern = new ReturnPatternFunction();
	public final static BMatchFunction TryPattern = new TryPatternFunction();
	public final static BMatchFunction ThrowPattern = new ThrowPatternFunction();

	public final static BMatchFunction NamePattern = new NamePatternFunction();
	public final static BMatchFunction VarPattern = new VarPatternFunction();
	public final static BMatchFunction ParamPattern = new ParamPatternFunction();
	public final static BMatchFunction FunctionPattern = new FunctionPatternFunction();
	public final static BMatchFunction PrototypePattern = new PrototypePatternFunction();

	public final static BMatchFunction LetPattern = new LetPatternFunction();
	public final static BMatchFunction ExportPattern = new ExportPatternFunction();
	public final static BMatchFunction ImportPattern = new ImportPatternFunction();

	public final static BMatchFunction ClassPattern = new ClassPatternFunction();
	public final static BMatchFunction ClassFieldPattern = new FieldPatternFunction();
	public final static BMatchFunction InstanceOfPattern = new InstanceOfPatternFunction();

	public final static BMatchFunction AssertPattern = new AssertPatternFunction();
	public final static BMatchFunction RequirePattern = new RequirePatternFunction();

	public final static BMatchFunction AsmPattern = new AsmPatternFunction();
	public final static BMatchFunction DefinePattern = new BunDefinePatternFunction();
	public final static BMatchFunction DefineNamePattern = new BunDefineNamePatternFunction();

	public static void ImportGrammar(BNameSpace NameSpace) {
		NameSpace.SetTypeName(BType.VoidType,  null);
		NameSpace.SetTypeName(BType.BooleanType, null);
		NameSpace.SetTypeName(BType.IntType, null);
		NameSpace.SetTypeName(BType.FloatType, null);
		NameSpace.SetTypeName(BType.StringType, null);
		//NameSpace.SetTypeName(ZType.TypeType, null);
		NameSpace.SetTypeName(BGenericType._AlphaType, null);
		NameSpace.SetTypeName(BGenericType._ArrayType, null);
		NameSpace.SetTypeName(BGenericType._MapType, null);
		NameSpace.SetTypeName(BFuncType._FuncType, null);

		NameSpace.AppendTokenFunc(" \t", WhiteSpaceToken);
		NameSpace.AppendTokenFunc("\n",  NewLineToken);
		NameSpace.AppendTokenFunc("{}()[]<>.,;?:+-*/%=&|!@~^$", OperatorToken);
		NameSpace.AppendTokenFunc("/", BlockComment);  // overloading
		NameSpace.AppendTokenFunc("Aa_", NameToken);

		NameSpace.AppendTokenFunc("\"", StringLiteralToken);
		NameSpace.AppendTokenFunc("1",  NumberLiteralToken);

		NameSpace.DefineExpression("null", NullPattern);
		NameSpace.DefineExpression("true", TruePattern);
		NameSpace.DefineExpression("false", FalsePattern);

		NameSpace.DefineExpression("+", PlusPattern);
		NameSpace.DefineExpression("-", MinusPattern);
		NameSpace.DefineExpression("~", ComplementPattern);
		NameSpace.DefineExpression("!", NotPattern);
		//		NameSpace.AppendSyntax("++ --", new Incl"));

		NameSpace.DefineRightExpression("==", EqualsPattern);
		NameSpace.DefineRightExpression("!=", NotEqualsPattern);
		NameSpace.DefineRightExpression("<", LessThanPattern);
		NameSpace.DefineRightExpression("<=", LessThanEqualsPattern);
		NameSpace.DefineRightExpression(">", GreaterThanPattern);
		NameSpace.DefineRightExpression(">=", GreaterThanEqualsPattern);

		NameSpace.DefineRightExpression("+", AddPattern);
		NameSpace.DefineRightExpression("-", SubPattern);
		NameSpace.DefineRightExpression("*", MulPattern);
		NameSpace.DefineRightExpression("/", DivPattern);
		NameSpace.DefineRightExpression("%", ModPattern);

		NameSpace.DefineRightExpression("<<", LeftShiftPattern);
		NameSpace.DefineRightExpression(">>", RightShiftPattern);

		NameSpace.DefineRightExpression("&", BitwiseAndPattern);
		NameSpace.DefineRightExpression("|", BitwiseOrPattern);
		NameSpace.DefineRightExpression("^", BitwiseXorPattern);

		NameSpace.DefineRightExpression("&&", AndPattern);
		NameSpace.DefineRightExpression("||", OrPattern);

		NameSpace.DefineExpression("$StringLiteral$", StringLiteralPattern);
		NameSpace.DefineExpression("$IntegerLiteral$", IntLiteralPattern);
		NameSpace.DefineExpression("$FloatLiteral$", FloatLiteralPattern);

		NameSpace.DefineExpression("$Type$", TypePattern);
		NameSpace.DefineExpression("$OpenType$", OpenTypePattern);
		NameSpace.DefineExpression("$TypeRight$", TypeSuffixPattern);
		NameSpace.DefineExpression("$TypeAnnotation$", TypeAnnotationPattern);

		NameSpace.DefineRightExpression(".", GetFieldPattern);
		NameSpace.DefineRightExpression(".", SetFieldPattern);
		NameSpace.DefineRightExpression(".", MethodCallPattern);

		NameSpace.DefineExpression("(", GroupPattern);
		NameSpace.DefineExpression("(", CastPattern);
		NameSpace.DefineRightExpression("(", FuncCallPattern);

		NameSpace.DefineRightExpression("[", GetIndexPattern);
		NameSpace.DefineRightExpression("[", SetIndexPattern);
		NameSpace.DefineExpression("[", ArrayLiteralPattern);
		NameSpace.DefineExpression("$MapEntry$", MapEntryPattern);
		NameSpace.DefineExpression("{", MapLiteralPattern);
		NameSpace.DefineExpression("new", NewObjectPattern);

		NameSpace.DefineStatement(";", StatementEndPattern);
		NameSpace.DefineExpression("$Block$", BlockPattern);
		NameSpace.DefineExpression("$Annotation$", AnnotationPattern);
		NameSpace.DefineExpression("$SymbolExpression$", SymbolExpressionPattern);
		// don't change DefineStatement for $SymbolStatement$
		NameSpace.DefineExpression("$SymbolStatement$", SymbolStatementPattern);
		NameSpace.DefineExpression("$Statement$", StatementPattern);
		NameSpace.DefineExpression("$Expression$", ExpressionPattern);
		NameSpace.DefineExpression("$RightExpression$", RightExpressionPattern);
		NameSpace.DefineExpression("$InStatement$", InStatementPattern);

		NameSpace.DefineStatement("if", IfPattern);
		NameSpace.DefineStatement("return", ReturnPattern);
		NameSpace.DefineStatement("while", WhilePattern);
		NameSpace.DefineStatement("break", BreakPattern);

		NameSpace.DefineExpression("$Name$", NamePattern);
		NameSpace.DefineStatement("var", VarPattern);
		NameSpace.DefineExpression("$Param$", ParamPattern);
		NameSpace.DefineExpression("function", PrototypePattern);
		NameSpace.DefineExpression("function", FunctionPattern);

		NameSpace.DefineStatement("let", LetPattern);
		NameSpace.DefineStatement("export", ExportPattern);

		NameSpace.SetTypeName(BClassType._ObjectType, null);
		NameSpace.DefineStatement("class", ClassPattern);
		NameSpace.DefineExpression("$FieldDecl$", ClassFieldPattern);
		//		NameSpace.DefineRightExpression("instanceof", BunPrecedence._Instanceof, InstanceOfPattern);
		NameSpace.DefineRightExpression("instanceof", InstanceOfPattern);

		NameSpace.DefineStatement("assert", AssertPattern);
		NameSpace.DefineStatement("require", RequirePattern);

		NameSpace.DefineStatement("asm", AsmPattern);
		NameSpace.DefineStatement("$DefineName$", DefineNamePattern);
		NameSpace.DefineStatement("define", DefinePattern);
		NameSpace.Generator.LangInfo.AppendGrammarInfo("zen-0.1");

		NameSpace.DefineStatement("try", TryPattern);
		NameSpace.DefineStatement("throw", ThrowPattern);
		NameSpace.Generator.LangInfo.AppendGrammarInfo("zen-trycatch-0.1");

	}

	public final static BSyntax _GetRightPattern(BNameSpace NameSpace, BTokenContext TokenContext) {
		@Var BToken Token = TokenContext.GetToken();
		if(Token != BToken._NullToken) {
			@Var BSyntax Pattern = NameSpace.GetRightSyntaxPattern(Token.GetText());
			return Pattern;
		}
		return null;
	}

	public final static BNode _DispatchPattern(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode, boolean AllowStatement, boolean AllowBinary) {
		@Var BToken Token = TokenContext.GetToken();
		@Var BSyntax Pattern = null;
		@Var BNameSpace NameSpace = ParentNode.GetNameSpace();
		if(Token instanceof BPatternToken) {
			Pattern = ((BPatternToken)Token).PresetPattern;
		}
		else {
			Pattern = NameSpace.GetSyntaxPattern(Token.GetText());
		}
		//System.out.println("Pattern=" + Pattern + " by '" + Token.GetText() + "'");
		if(Pattern != null) {
			if(Pattern.IsStatement && !AllowStatement) {
				return new ErrorNode(ParentNode, Token, Token.GetText() + " statement is not here");
			}
			LeftNode = TokenContext.ApplyMatchPattern(ParentNode, LeftNode, Pattern, BTokenContext._Required);
		}
		else {
			if(Token.IsNameSymbol()) {
				if(AllowStatement) {
					Pattern = NameSpace.GetSyntaxPattern("$SymbolStatement$");
				}
				else {
					Pattern = NameSpace.GetSyntaxPattern("$SymbolExpression$");
				}
				LeftNode = TokenContext.ApplyMatchPattern(ParentNode, LeftNode, Pattern, BTokenContext._Required);
			}
			else {
				if(AllowStatement) {
					return TokenContext.CreateExpectedErrorNode(Token, "statement");
				}
				else {
					return TokenContext.CreateExpectedErrorNode(Token, "expression");
				}
			}
		}
		if(!Pattern.IsStatement) {
			while(LeftNode != null && !LeftNode.IsErrorNode()) {
				@Var BSyntax RightPattern = _GetRightPattern(NameSpace, TokenContext);
				if(RightPattern == null) {
					break;
				}
				if(!AllowBinary && RightPattern.IsBinaryOperator()) {
					break;
				}
				LeftNode = TokenContext.ApplyMatchPattern(ParentNode, LeftNode, RightPattern, BTokenContext._Required);
			}
		}
		return LeftNode;
	}

}
