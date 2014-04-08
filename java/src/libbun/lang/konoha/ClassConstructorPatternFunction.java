package libbun.lang.konoha;

import libbun.ast.BNode;
import libbun.ast.decl.BClassNode;
import libbun.ast.decl.BFunctionNode;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ClassConstructorPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		String Name = "constructor";
		if(ParentNode instanceof BClassNode) {
			Name = ((BClassNode)ParentNode).ClassName();
		}
		ClassMemberNode MemberNode = new ClassMemberNode(ParentNode);
		boolean FoundQualifer = true;
		while(FoundQualifer) {
			FoundQualifer = false;
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
		}
		@Var BFunctionNode FunctionNode = new BFunctionNode(ParentNode);
		FunctionNode.GivenName = Name;
		@Var BNode FuncNode = FunctionNode;
		FuncNode = TokenContext.MatchToken(ParentNode, Name, BTokenContext._Required);
		FuncNode = TokenContext.MatchNtimes(FuncNode, "(", "$Param$", ",", ")");
		FuncNode = TokenContext.MatchPattern(FuncNode, BFunctionNode._Block, "$Block$", BTokenContext._Required);
		if(!FuncNode.IsErrorNode()) {
			MemberNode.ConstructorNode = FunctionNode;
			return MemberNode;
		}
		return FuncNode;
	}
}
