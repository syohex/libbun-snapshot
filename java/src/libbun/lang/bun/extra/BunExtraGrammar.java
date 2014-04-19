package libbun.lang.bun.extra;

import libbun.ast.BNode;
import libbun.ast.ContainerNode;
import libbun.ast.binary.BinaryOperatorNode;
import libbun.ast.binary.BunAddNode;
import libbun.ast.binary.AssignNode;
import libbun.ast.statement.BunWhileNode;
import libbun.ast.sugar.BunContinueNode;
import libbun.parser.BTokenContext;
import libbun.parser.LibBunGamma;
import libbun.util.BMatchFunction;
import libbun.util.Var;

// continue
class ContinuePatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode ContinueNode = new BunContinueNode(ParentNode);
		ContinueNode = TokenContext.MatchToken(ContinueNode, "continue", BTokenContext._Required);
		return ContinueNode;
	}
}

class ForPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode InitStmtNode = null;
		@Var BNode WhileNode = new BunWhileNode(ParentNode);
		WhileNode = TokenContext.MatchToken(WhileNode, "for", BTokenContext._Required);
		WhileNode = TokenContext.MatchToken(WhileNode, "(", BTokenContext._Required);
		if(!TokenContext.IsToken(";")) {
			InitStmtNode = TokenContext.ParsePattern(ParentNode, "$InStatement$", BTokenContext._Required);
		}
		WhileNode = TokenContext.MatchToken(WhileNode, ";", BTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, BunWhileNode._Cond, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		WhileNode = TokenContext.MatchToken(WhileNode, ";", BTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, BunWhileNode._Next, "$InStatement$", BTokenContext._Optional, BTokenContext._AllowSkipIndent);
		WhileNode = TokenContext.MatchToken(WhileNode, ")", BTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, BunWhileNode._Block, "$Block$", BTokenContext._Required);
		if(InitStmtNode == null) {
			return WhileNode;
		}
		return new ContainerNode(InitStmtNode, WhileNode);
	}
}

class ForInPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode WhileNode = new BunWhileNode(ParentNode);
		WhileNode = TokenContext.MatchToken(WhileNode, "for", BTokenContext._Required);
		WhileNode = TokenContext.MatchToken(WhileNode, "(", BTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, BunWhileNode._Next, "$InStatement$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		WhileNode = TokenContext.MatchToken(WhileNode, "in", BTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, BunWhileNode._Cond, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		WhileNode = TokenContext.MatchToken(WhileNode, ")", BTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, BunWhileNode._Block, "$Block$", BTokenContext._Required);
		return WhileNode;
	}
}

class DoWhilePatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		@Var BNode WhileNode = new BunWhileNode(ParentNode);
		WhileNode = TokenContext.MatchToken(WhileNode, "do", BTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, BunWhileNode._Block, "$Block$", BTokenContext._Required);
		WhileNode = TokenContext.MatchToken(WhileNode, "while", BTokenContext._Required);
		WhileNode = TokenContext.MatchToken(WhileNode, "(", BTokenContext._Required);
		WhileNode = TokenContext.MatchPattern(WhileNode, BunWhileNode._Cond, "$Expression$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		if(TokenContext.MatchNewLineToken("whatever")) {
			WhileNode = TokenContext.MatchPattern(WhileNode, BunWhileNode._Next, "$InStatement$", BTokenContext._Required, BTokenContext._AllowSkipIndent);
		}
		WhileNode = TokenContext.MatchToken(WhileNode, ")", BTokenContext._Required);
		return WhileNode;
	}
}

class SelfAddPatternFunction extends BMatchFunction {
	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		TokenContext.SkipToken();
		if(TokenContext.IsToken("=")) {
			@Var AssignNode AssignNode = new AssignNode(ParentNode);
			@Var BinaryOperatorNode BinaryNode = new BunAddNode(AssignNode);
			@Var BNode RightNode = BinaryNode.SetParsedNode(AssignNode, LeftNode, "=", TokenContext);
			AssignNode.SetLeftNode(LeftNode);
			AssignNode.SetLeftNode(RightNode);
			return AssignNode;
		}
		return null;
	}
}

public class BunExtraGrammar {
	public final static BMatchFunction ContinuePattern = new ContinuePatternFunction();

	public static void LoadGrammar(LibBunGamma Gamma) {
		//Gamma.SetTypeName(BType.VoidType,  null);
		//Gamma.AppendTokenFunc(" \t", WhiteSpaceToken);
		//Gamma.DefineExpression("null", NullPattern);
		//Gamma.DefineRightExpression("instanceof", InstanceOfPattern);
		Gamma.DefineStatement("continue", ContinuePattern);
	}
}
