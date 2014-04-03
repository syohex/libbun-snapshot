package libbun.lang.bun;

import libbun.parser.ZToken;
import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZBlockNode;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class BlockPatternFunction extends ZMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, ZTokenContext TokenContext, BNode LeftNode) {
		@Var BNode BlockNode = new ZBlockNode(ParentNode, null);
		@Var ZToken SkipToken = TokenContext.GetToken();
		BlockNode = TokenContext.MatchToken(BlockNode, "{", ZTokenContext._Required);
		if(!BlockNode.IsErrorNode()) {
			@Var boolean Remembered = TokenContext.SetParseFlag(ZTokenContext._AllowSkipIndent); // init
			@Var BNode NestedBlockNode = BlockNode;
			while(TokenContext.HasNext()) {
				//System.out.println("Token :" + TokenContext.GetToken());
				if(TokenContext.MatchToken("}")) {
					break;
				}
				NestedBlockNode = TokenContext.MatchPattern(NestedBlockNode, BNode._NestedAppendIndex, "$Statement$", ZTokenContext._Required);
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
