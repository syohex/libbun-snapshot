package libbun.lang.bun;

import libbun.parser.ZTokenContext;
import libbun.parser.ast.ZNode;
import libbun.util.Var;
import libbun.util.ZMatchFunction;

public class StatementPatternFunction extends ZMatchFunction {

	@Override public ZNode Invoke(ZNode ParentNode, ZTokenContext TokenContext, ZNode LeftNode) {
		@Var boolean Rememberd = TokenContext.SetParseFlag(ZTokenContext._AllowSkipIndent);
		//		@Var ZAnnotationNode AnnotationNode = (ZAnnotationNode)TokenContext.ParsePattern(ParentNode, "$Annotation$", ZTokenContext.Optional2);
		TokenContext.SetParseFlag(ZTokenContext._NotAllowSkipIndent);
		@Var ZNode StmtNode = ExpressionPatternFunction._DispatchPattern(ParentNode, TokenContext, null, true, true);
		StmtNode = TokenContext.MatchPattern(StmtNode, ZNode._Nop, ";", ZTokenContext._Required);
		//		if(AnnotationNode != null) {
		//			AnnotationNode.Append(StmtNode);
		//			StmtNode = AnnotationNode;
		//		}
		TokenContext.SetParseFlag(Rememberd);
		return StmtNode;
	}

}
