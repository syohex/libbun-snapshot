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
import libbun.ast.binary.BAndNode;
import libbun.ast.binary.BBinaryNode;
import libbun.ast.binary.BOrNode;
import libbun.ast.binary.BunAddNode;
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
import libbun.ast.binary.BunRightShiftNode;
import libbun.ast.binary.BunSubNode;
import libbun.parser.BNameSpace;
import libbun.parser.BTokenContext;
import libbun.type.BClassType;
import libbun.type.BFuncType;
import libbun.type.BGenericType;
import libbun.type.BType;
import libbun.util.BMatchFunction;
import libbun.util.Var;


class BunAndPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BAndNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunOrPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BOrNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunAddPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunAddNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunSubPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunSubNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunMulPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunMulNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunDivPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunDivNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunModPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunModNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunBitwiseAndPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunBitwiseAndNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunBitwiseOrPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunBitwiseOrNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunBitwiseXorPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunBitwiseXorNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunLeftShiftPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunLeftShiftNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunRightShiftPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunRightShiftNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunEqualsPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunEqualsNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunNotEqualsPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunNotEqualsNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunLessThanPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunLessThanNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunLessThanEqualsPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunLessThanEqualsNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunGreaterThanPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunGreaterThanNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

class BunGreaterThanEqualsPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BBinaryNode BinaryNode = new BunGreaterThanEqualsNode(ParentNode, TokenContext.GetToken(BTokenContext._MoveNext), LeftNode);
		return BinaryNode.AppendParsedRightNode(ParentNode, TokenContext);
	}
}

public class BunGrammar {
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

		NameSpace.AppendTokenFunc(" \t", new WhiteSpaceTokenFunction());
		NameSpace.AppendTokenFunc("\n",  new NewLineTokenFunction());
		NameSpace.AppendTokenFunc("{}()[]<>.,;?:+-*/%=&|!@~^$", new OperatorTokenFunction());
		NameSpace.AppendTokenFunc("/", new BlockCommentFunction());  // overloading
		NameSpace.AppendTokenFunc("Aa_", new NameTokenFunction());

		NameSpace.AppendTokenFunc("\"", new StringLiteralTokenFunction());
		NameSpace.AppendTokenFunc("1",  new NumberLiteralTokenFunction());

		@Var BMatchFunction MatchUnary = new UnaryPatternFunction();
		@Var BMatchFunction MatchBinary = new BinaryPatternFunction();
		//		@Var BMatchFunction MatchComparator = new ComparatorPatternFunction();

		NameSpace.DefineExpression("null", new NullPatternFunction());
		NameSpace.DefineExpression("true", new TruePatternFunction());
		NameSpace.DefineExpression("false", new FalsePatternFunction());

		NameSpace.DefineExpression("+", MatchUnary);
		NameSpace.DefineExpression("-", MatchUnary);
		NameSpace.DefineExpression("~", MatchUnary);
		NameSpace.DefineExpression("!", new NotPatternFunction());
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

		NameSpace.DefineExpression("$Type$",new DefinedTypePatternFunction());
		NameSpace.DefineExpression("$OpenType$",new OpenTypePatternFunction());
		NameSpace.DefineExpression("$TypeRight$", new RightTypePatternFunction());
		NameSpace.DefineExpression("$TypeAnnotation$", new TypeAnnotationPatternFunction());

		NameSpace.DefineExpression("$StringLiteral$", new StringLiteralPatternFunction());
		NameSpace.DefineExpression("$IntegerLiteral$", new IntLiteralPatternFunction());
		NameSpace.DefineExpression("$FloatLiteral$", new FloatLiteralPatternFunction());

		NameSpace.DefineRightExpression(".", new GetterPatternFunction());
		NameSpace.DefineRightExpression(".", new SetterPatternFunction());
		NameSpace.DefineRightExpression(".", new MethodCallPatternFunction());

		NameSpace.DefineExpression("(", new GroupPatternFunction());
		NameSpace.DefineExpression("(", new CastPatternFunction());
		NameSpace.DefineRightExpression("(", new ApplyPatternFunction());

		NameSpace.DefineRightExpression("[", new GetIndexPatternFunction());
		NameSpace.DefineRightExpression("[", new SetIndexPatternFunction());
		NameSpace.DefineExpression("[", new ArrayLiteralPatternFunction());
		NameSpace.DefineExpression("$MapEntry$", new MapEntryPatternFunction());
		NameSpace.DefineExpression("{", new MapLiteralPatternFunction());
		NameSpace.DefineExpression("new", new NewObjectPatternFunction());

		NameSpace.DefineStatement(";", new StatementEndPatternFunction());
		NameSpace.DefineExpression("$Block$", new BlockPatternFunction());
		NameSpace.DefineExpression("$Annotation$", new AnnotationPatternFunction());
		NameSpace.DefineExpression("$SymbolExpression$", new SymbolExpressionPatternFunction());
		// don't change DefineStatement for $SymbolStatement$
		NameSpace.DefineExpression("$SymbolStatement$", new SymbolStatementPatternFunction());
		NameSpace.DefineExpression("$Statement$", new StatementPatternFunction());
		NameSpace.DefineExpression("$Expression$", new ExpressionPatternFunction());
		NameSpace.DefineExpression("$RightExpression$", new RightExpressionPatternFunction());
		NameSpace.DefineExpression("$InStatement$", new InStatementPatternFunction());

		NameSpace.DefineStatement("if", new IfPatternFunction());
		NameSpace.DefineStatement("return", new ReturnPatternFunction());
		NameSpace.DefineStatement("while", new WhilePatternFunction());
		NameSpace.DefineStatement("break", new BreakPatternFunction());

		NameSpace.DefineExpression("$Name$", new NamePatternFunction());
		NameSpace.DefineStatement("var",  new VarPatternFunction());
		NameSpace.DefineExpression("$Param$", new ParamPatternFunction());
		NameSpace.DefineExpression("function", new PrototypePatternFunction());
		NameSpace.DefineExpression("function", new FunctionPatternFunction());

		NameSpace.DefineStatement("let", new LetPatternFunction());
		NameSpace.DefineStatement("export", new ExportPatternFunction());

		NameSpace.SetTypeName(BClassType._ObjectType, null);
		NameSpace.DefineStatement("class", new ClassPatternFunction());
		NameSpace.DefineExpression("$FieldDecl$", new FieldPatternFunction());
		//		NameSpace.DefineRightExpression("instanceof", BunPrecedence._Instanceof, new InstanceOfPatternFunction());
		NameSpace.DefineRightExpression("instanceof", new InstanceOfPatternFunction());

		NameSpace.DefineStatement("assert", new AssertPatternFunction());
		NameSpace.DefineStatement("require", new RequirePatternFunction());

		NameSpace.DefineStatement("asm", new AsmPatternFunction());
		NameSpace.DefineStatement("$DefineName$", new BunDefineNamePatternFunction());
		NameSpace.DefineStatement("define", new BunDefinePatternFunction());
		NameSpace.Generator.LangInfo.AppendGrammarInfo("zen-0.1");

		NameSpace.DefineStatement("try", new TryPatternFunction());
		NameSpace.DefineStatement("throw", new ThrowPatternFunction());
		NameSpace.Generator.LangInfo.AppendGrammarInfo("zen-trycatch-0.1");

	}

}
