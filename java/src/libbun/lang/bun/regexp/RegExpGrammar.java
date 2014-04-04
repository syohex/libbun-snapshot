package libbun.lang.bun.regexp;

import libbun.parser.BNameSpace;
import libbun.type.BGenericType;


public class RegExpGrammar {
	public static void ImportGrammar(BNameSpace NameSpace) {
		NameSpace.SetTypeName(BGenericType._MapType, null);
		NameSpace.AppendTokenFunc("/", new RexExpLiteralTokenFunction());
		NameSpace.DefineExpression("$RexExpLiteral$", new RexExpLiteralPatternFunction());
		NameSpace.DefineExpression("$RexExpLiteralFlag$", null);
		NameSpace.DefineExpression("$RexExpLiteral$", new RexExpLiteralPatternFunction());
	}
}