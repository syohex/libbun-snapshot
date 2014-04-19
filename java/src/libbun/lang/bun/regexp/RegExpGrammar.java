package libbun.lang.bun.regexp;

import libbun.parser.LibBunGamma;
import libbun.type.BGenericType;


public class RegExpGrammar {
	public static void LoadGrammar(LibBunGamma Gamma) {
		Gamma.SetTypeName(BGenericType._MapType, null);
		Gamma.DefineToken("/", new RexExpLiteralTokenFunction());
		Gamma.DefineExpression("$RexExpLiteral$", new RexExpLiteralPatternFunction());
		Gamma.DefineExpression("$RexExpLiteralFlag$", null);
		Gamma.DefineExpression("$RexExpLiteral$", new RexExpLiteralPatternFunction());
	}
}