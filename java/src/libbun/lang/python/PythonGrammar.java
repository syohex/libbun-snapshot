package libbun.lang.python;

import libbun.lang.bun.BunGrammar;
import libbun.parser.BNameSpace;

public class PythonGrammar {
	public static void ImportGrammar(BNameSpace NameSpace) {
		BunGrammar.ImportGrammar(NameSpace);

		NameSpace.DefineExpression("None", BunGrammar.NullPattern);
		NameSpace.DefineExpression("True", BunGrammar.TruePattern);
		NameSpace.DefineExpression("False", BunGrammar.FalsePattern);

		NameSpace.DefineExpression("def", new PythonFunctionPatternFunction());
		NameSpace.DefineExpression("if", new PythonIfPatternFunction());
		NameSpace.DefineExpression("while", new PythonWhilePatternFunction());
		NameSpace.DefineExpression("$Block$", new PythonBlockPatternFunction());
		NameSpace.DefineExpression("$Statement$", new PythonStatementPatternFunction());
		NameSpace.DefineExpression("$Param$", new PythonParamPatternFunction());

		NameSpace.DefineRightExpression("and", BunGrammar.AndPattern);
		NameSpace.DefineRightExpression("or", BunGrammar.OrPattern);
		//TODO is, is not

		NameSpace.AppendTokenFunc("#", new PythonCommentFunction());


		NameSpace.Generator.LangInfo.AppendGrammarInfo("python");
	}
}
