package libbun.lang.konoha;

import libbun.lang.bun.BunGrammar;
import libbun.parser.BNameSpace;

public class KonohaGrammar {
	public static void ImportGrammar(BNameSpace NameSpace) {
		BunGrammar.ImportGrammar(NameSpace);
		NameSpace.DefineStatement("continue", new ContinuePatternFunction());

	}
}
