package libbun.lang.konoha;

import libbun.ast.BNode;
import libbun.ast.decl.BFunctionNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ClassMethodPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		ClassMemberNode MemberNode = new ClassMemberNode(ParentNode);
		boolean FoundQualifer = true;
		while(FoundQualifer) {
			FoundQualifer = false;
			if(TokenContext.MatchToken("@Override")) {
				FoundQualifer = true;
			}
			if(TokenContext.MatchNewLineToken("public")) {
				MemberNode.IsPublic = true;
				FoundQualifer = true;
			}
			if(TokenContext.MatchNewLineToken("protected")) {
				FoundQualifer = true;
			}
			if(TokenContext.MatchNewLineToken("private")) {
				FoundQualifer = true;
			}
			if(TokenContext.MatchNewLineToken("final")) {
				MemberNode.IsFinal = true;
				FoundQualifer = true;
			}
			if(TokenContext.MatchNewLineToken("abstract")) {
				MemberNode.IsAbstract = true;
				FoundQualifer = true;
			}
			if(TokenContext.MatchNewLineToken("static")) {
				MemberNode.IsStatic = true;
				FoundQualifer = true;
			}
		}
		@Var BFunctionNode FunctionNode = new BFunctionNode(ParentNode);
		@Var BNode FuncNode = FunctionNode;
		FuncNode = TokenContext.MatchPattern(FuncNode, BFunctionNode._TypeInfo, "$OpenType$", BTokenContext._Required);
		FuncNode = TokenContext.MatchPattern(FuncNode, BFunctionNode._NameInfo, "$Name$", BTokenContext._Required);
		FuncNode = TokenContext.MatchNtimes(FuncNode, "(", "$Param$", ",", ")");
		FuncNode = TokenContext.MatchPattern(FuncNode, BFunctionNode._Block, "$Block$", BTokenContext._Required);
		if(!FuncNode.IsErrorNode()) {
			MemberNode.MethodNode = FunctionNode;
			return MemberNode;
		}
		return FuncNode;
	}
}
