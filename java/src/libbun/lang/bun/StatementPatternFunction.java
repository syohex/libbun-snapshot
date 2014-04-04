package libbun.lang.bun;

import libbun.parser.BTokenContext;
import libbun.parser.ast.BNode;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class StatementPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var boolean Rememberd = TokenContext.SetParseFlag(BTokenContext._AllowSkipIndent);
		//		@Var ZAnnotationNode AnnotationNode = (ZAnnotationNode)TokenContext.ParsePattern(ParentNode, "$Annotation$", ZTokenContext.Optional2);
		TokenContext.SetParseFlag(BTokenContext._NotAllowSkipIndent);
		@Var BNode StmtNode = ExpressionPatternFunction._DispatchPattern(ParentNode, TokenContext, null, true, true);
		StmtNode = TokenContext.MatchPattern(StmtNode, BNode._Nop, ";", BTokenContext._Required);
		//		if(AnnotationNode != null) {
		//			AnnotationNode.Append(StmtNode);
		//			StmtNode = AnnotationNode;
		//		}
		TokenContext.SetParseFlag(Rememberd);
		return StmtNode;
	}

}
