package libbun.lang.konoha;

import libbun.ast.BNode;
import libbun.ast.decl.BLetVarNode;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;

public class ClassFieldPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		//		String Name = "constructor";
		//		if(ParentNode instanceof ZClassNode) {
		//			Name = ((ZClassNode)ParentNode).ClassName();
		//		}
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
			if(TokenContext.MatchNewLineToken("final")) {
				MemberNode.IsFinal = true;
				FoundQualifer = true;
			}
			if(TokenContext.MatchNewLineToken("static")) {
				MemberNode.IsStatic = true;
				FoundQualifer = true;
			}
		}
		BLetVarNode Node = new BLetVarNode(ParentNode, 0, null, null);
		BNode FieldNode = MemberNode;
		FieldNode = TokenContext.MatchPattern(FieldNode, BLetVarNode._TypeInfo, "$OpenType$", BTokenContext._Required);
		FieldNode = TokenContext.MatchPattern(FieldNode, BLetVarNode._NameInfo, "$Name$", BTokenContext._Required);
		if(TokenContext.MatchToken("=")) {
			FieldNode = TokenContext.MatchPattern(FieldNode, BLetVarNode._InitValue, "$Expr$", BTokenContext._Required);
		}
		if(!FieldNode.IsErrorNode()) {
			MemberNode.FieldNode = Node;
			return MemberNode;
		}
		return FieldNode;
	}
}
