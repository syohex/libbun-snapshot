package libbun.lang.bun.regexp;

import libbun.parser.LibBunGamma;
import libbun.type.BGenericType;


public class RegExpGrammar {
	public static void ImportGrammar(LibBunGamma Gamma) {
		Gamma.SetTypeName(BGenericType._MapType, null);
		Gamma.AppendTokenFunc("/", new RexExpLiteralTokenFunction());
		Gamma.DefineExpression("$RexExpLiteral$", new RexExpLiteralPatternFunction());
		Gamma.DefineExpression("$RexExpLiteralFlag$", null);
		Gamma.DefineExpression("$RexExpLiteral$", new RexExpLiteralPatternFunction());
	}
}