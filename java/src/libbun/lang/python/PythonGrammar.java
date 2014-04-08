package libbun.lang.python;

import libbun.lang.bun.BunGrammar;
import libbun.lang.bun.FalsePatternFunction;
import libbun.lang.bun.NullPatternFunction;
import libbun.lang.bun.TruePatternFunction;
import libbun.parser.BNameSpace;

public class PythonGrammar {
	public static void ImportGrammar(BNameSpace NameSpace) {
		BunGrammar.ImportGrammar(NameSpace);

		NameSpace.DefineExpression("None", new NullPatternFunction());
		NameSpace.DefineExpression("True", new TruePatternFunction());
		NameSpace.DefineExpression("False", new FalsePatternFunction());

		NameSpace.DefineExpression("def", new PythonFunctionPatternFunction());
		NameSpace.DefineExpression("if", new PythonIfPatternFunction());
		NameSpace.DefineExpression("$Block$", new PythonBlockPatternFunction());
		NameSpace.DefineExpression("$Statement$", new PythonStatementPatternFunction());

		NameSpace.Generator.LangInfo.AppendGrammarInfo("python");
	}
}
