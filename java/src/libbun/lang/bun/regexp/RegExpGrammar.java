package libbun.lang.bun.regexp;

import libbun.parser.ZNameSpace;
import libbun.type.ZGenericType;


public class RegExpGrammar {
	public static void ImportGrammar(ZNameSpace NameSpace) {
		NameSpace.SetTypeName(ZGenericType._MapType, null);
		NameSpace.AppendTokenFunc("/", new RexExpLiteralTokenFunction());
		NameSpace.DefineExpression("$RexExpLiteral$", new RexExpLiteralPatternFunction());
		NameSpace.DefineExpression("$RexExpLiteralFlag$", null);
		NameSpace.DefineExpression("$RexExpLiteral$", new RexExpLiteralPatternFunction());
	}
}