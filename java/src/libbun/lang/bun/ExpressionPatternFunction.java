package libbun.lang.bun;

import libbun.ast.BErrorNode;
import libbun.ast.BNode;
import libbun.parser.BNameSpace;
import libbun.parser.BPatternToken;
import libbun.parser.BSyntax;
import libbun.parser.BToken;
import libbun.parser.BTokenContext;
import libbun.util.Var;
import libbun.util.BMatchFunction;

public class ExpressionPatternFunction extends BMatchFunction {

	@Override public BNode Invoke(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode) {
		return ExpressionPatternFunction._DispatchPattern(ParentNode, TokenContext, LeftNode, false, true);
	}

	public final static BSyntax _GetRightPattern(BNameSpace NameSpace, BTokenContext TokenContext) {
		@Var BToken Token = TokenContext.GetToken();
		if(Token != BToken._NullToken) {
			@Var BSyntax Pattern = NameSpace.GetRightSyntaxPattern(Token.GetText());
			return Pattern;
		}
		return null;
	}

	public final static BNode _DispatchPattern(BNode ParentNode, BTokenContext TokenContext, BNode LeftNode, boolean AllowStatement, boolean AllowBinary) {
		@Var BToken Token = TokenContext.GetToken();
		@Var BSyntax Pattern = null;
		@Var BNameSpace NameSpace = ParentNode.GetNameSpace();
		if(Token instanceof BPatternToken) {
			Pattern = ((BPatternToken)Token).PresetPattern;
		}
		else {
			Pattern = NameSpace.GetSyntaxPattern(Token.GetText());
		}
		//System.out.println("Pattern=" + Pattern + " by '" + Token.GetText() + "'");
		if(Pattern != null) {
			if(Pattern.IsStatement && !AllowStatement) {
				return new BErrorNode(ParentNode, Token, Token.GetText() + " statement is not here");
			}
			LeftNode = TokenContext.ApplyMatchPattern(ParentNode, LeftNode, Pattern, BTokenContext._Required);
		}
		else {
			if(Token.IsNameSymbol()) {
				if(AllowStatement) {
					Pattern = NameSpace.GetSyntaxPattern("$SymbolStatement$");
				}
				else {
					Pattern = NameSpace.GetSyntaxPattern("$SymbolExpression$");
				}
				LeftNode = TokenContext.ApplyMatchPattern(ParentNode, LeftNode, Pattern, BTokenContext._Required);
			}
			else {
				if(AllowStatement) {
					return TokenContext.CreateExpectedErrorNode(Token, "statement");
				}
				else {
					return TokenContext.CreateExpectedErrorNode(Token, "expression");
				}
			}
		}
		if(!Pattern.IsStatement) {
			while(LeftNode != null && !LeftNode.IsErrorNode()) {
				@Var BSyntax RightPattern = ExpressionPatternFunction._GetRightPattern(NameSpace, TokenContext);
				if(RightPattern == null) {
					break;
				}
				if(!AllowBinary && RightPattern.IsBinaryOperator()) {
					break;
				}
				LeftNode = TokenContext.ApplyMatchPattern(ParentNode, LeftNode, RightPattern, BTokenContext._Required);
			}
		}
		return LeftNode;
	}

}
