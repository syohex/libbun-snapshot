package libbun.lang.konoha;

import libbun.lang.bun.BunGrammar;
import libbun.parser.ZNameSpace;

public class KonohaGrammar {
	public static void ImportGrammar(ZNameSpace NameSpace) {
		BunGrammar.ImportGrammar(NameSpace);
		NameSpace.DefineStatement("continue", new ContinuePatternFunction());

	}
}
