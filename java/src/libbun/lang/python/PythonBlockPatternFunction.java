package libbun.lang.python;

import libbun.ast.BBlockNode;
import libbun.ast.BNode;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.BMatchFunction;
import libbun.util.Var;

public class PythonBlockPatternFunction extends BMatchFunction {

	@Override
	public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode BlockNode = new BBlockNode(ParentNode, ParentNode.GetNameSpace());
		@Var BToken SkipToken = TokenContext.GetToken();
		BlockNode = TokenContext.MatchToken(BlockNode, ":", BTokenContext._Required);
		if(!BlockNode.IsErrorNode()) {
			@Var boolean Remembered = TokenContext.SetParseFlag(BTokenContext._AllowSkipIndent); // init
			@Var BNode NestedBlockNode = BlockNode;
			@Var int IndentSize = 0;
			while(TokenContext.HasNext()) {
				@Var BToken Token = TokenContext.GetToken();
				if(IndentSize > Token.GetIndentSize()) {
					break;
				}
				IndentSize = Token.GetIndentSize();
				NestedBlockNode = TokenContext.MatchPattern(NestedBlockNode, BNode._NestedAppendIndex, "$Statement$", BTokenContext._Required);
				if(NestedBlockNode.IsErrorNode()) {
					TokenContext.SkipError(SkipToken);
					break;
				}
			}
			TokenContext.SetParseFlag(Remembered);
		}
		return BlockNode;
	}

}
