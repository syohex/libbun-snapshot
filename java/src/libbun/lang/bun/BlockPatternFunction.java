package libbun.lang.bun;

import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class BlockPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode BlockNode = new ZBlockNode(ParentNode, null);
		@Var BToken SkipToken = TokenContext.GetToken();
		BlockNode = TokenContext.MatchToken(BlockNode, "{", BTokenContext._Required);
		if(!BlockNode.IsErrorNode()) {
			@Var boolean Remembered = TokenContext.SetParseFlag(BTokenContext._AllowSkipIndent); // init
			@Var BNode NestedBlockNode = BlockNode;
			while(TokenContext.HasNext()) {
				//System.out.println("Token :" + TokenContext.GetToken());
				if(TokenContext.MatchToken("}")) {
					break;
				}
				NestedBlockNode = TokenContext.MatchPattern(NestedBlockNode, BNode._NestedAppendIndex, "$Statement$", BTokenContext._Required);
				if(NestedBlockNode.IsErrorNode()) {
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
